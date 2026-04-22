"""
Entity Ordering Optimizer

This module provides algorithms to optimize the ordering of entities in diagrams
to minimize line crossings and reduce visual bunching.
"""

import networkx as nx
from typing import List, Dict, Any, Set, Tuple
from collections import defaultdict, deque

def analyze_entity_relationships(entities: List[Dict[str, Any]], relationships: List[Dict[str, Any]]) -> Dict[str, Any]:
    """
    Analyze the relationship graph to understand entity connections.
    
    Returns:
        Dict containing analysis results including centrality measures and clustering info
    """
    # Create a graph from relationships
    G = nx.Graph()
    
    # Add all entities as nodes
    entity_names = [entity['name'] for entity in entities]
    G.add_nodes_from(entity_names)
    
    # Add edges from relationships
    for rel in relationships:
        if rel['from'] in entity_names and rel['to'] in entity_names:
            G.add_edge(rel['from'], rel['to'])
    
    # Calculate centrality measures
    degree_centrality = nx.degree_centrality(G)
    betweenness_centrality = nx.betweenness_centrality(G)
    closeness_centrality = nx.closeness_centrality(G)
    
    # Find connected components
    components = list(nx.connected_components(G))
    
    # Calculate clustering coefficient
    clustering = nx.clustering(G)
    
    return {
        'graph': G,
        'degree_centrality': degree_centrality,
        'betweenness_centrality': betweenness_centrality,
        'closeness_centrality': closeness_centrality,
        'components': components,
        'clustering': clustering,
        'num_components': len(components)
    }

def calculate_entity_importance(entity: Dict[str, Any], analysis: Dict[str, Any]) -> float:
    """
    Calculate importance score for an entity based on various factors.
    
    Higher scores indicate more central/important entities that should be placed prominently.
    """
    entity_name = entity['name']
    
    # Base importance factors
    importance = 0.0
    
    # Number of foreign keys (entities that reference others)
    fk_count = len(entity.get('foreign_keys', []))
    importance += fk_count * 2.0  # Higher weight for entities with FKs
    
    # Number of columns (complexity)
    column_count = len(entity.get('columns', []))
    importance += column_count * 0.1
    
    # Centrality measures from graph analysis
    if entity_name in analysis['degree_centrality']:
        importance += analysis['degree_centrality'][entity_name] * 10.0
        importance += analysis['betweenness_centrality'][entity_name] * 5.0
        importance += analysis['closeness_centrality'][entity_name] * 3.0
    
    # Inheritance bonus (mapped superclasses should be prominent)
    if entity.get('is_mapped_superclass', False):
        importance += 5.0
    
    # Primary key bonus
    if entity.get('primary_key'):
        importance += 1.0
    
    return importance

def find_optimal_ordering_hierarchical(entities: List[Dict[str, Any]], relationships: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
    """
    Find optimal entity ordering using a hierarchical approach.
    
    Places most central entities first, then groups related entities nearby.
    """
    analysis = analyze_entity_relationships(entities, relationships)
    
    # Calculate importance scores
    entity_scores = []
    for entity in entities:
        score = calculate_entity_importance(entity, analysis)
        entity_scores.append((entity, score))
    
    # Sort by importance (highest first)
    entity_scores.sort(key=lambda x: x[1], reverse=True)
    
    # Group entities by connected components
    components = analysis['components']
    component_map = {}
    for i, component in enumerate(components):
        for entity_name in component:
            component_map[entity_name] = i
    
    # Separate entities by component and sort each component internally
    component_entities = defaultdict(list)
    for entity, score in entity_scores:
        comp_id = component_map.get(entity['name'], -1)
        component_entities[comp_id].append((entity, score))
    
    # Order components by the importance of their most important entity
    component_order = []
    for comp_id, comp_entities in component_entities.items():
        max_score = max(score for _, score in comp_entities)
        component_order.append((comp_id, max_score))
    
    component_order.sort(key=lambda x: x[1], reverse=True)
    
    # Build final ordering
    ordered_entities = []
    for comp_id, _ in component_order:
        # Sort entities within component by importance
        comp_entities = component_entities[comp_id]
        comp_entities.sort(key=lambda x: x[1], reverse=True)
        ordered_entities.extend([entity for entity, _ in comp_entities])
    
    return ordered_entities

def find_optimal_ordering_force_directed(entities: List[Dict[str, Any]], relationships: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
    """
    Find optimal entity ordering using a force-directed approach.
    
    Simulates forces between connected entities to minimize crossings.
    """
    analysis = analyze_entity_relationships(entities, relationships)
    G = analysis['graph']
    
    # Use spring layout to get optimal 2D positions
    try:
        pos = nx.spring_layout(G, k=3, iterations=100)
    except:
        # Fallback if spring layout fails
        pos = {entity['name']: (i, 0) for i, entity in enumerate(entities)}
    
    # Sort entities by x-coordinate from spring layout
    entity_positions = []
    for entity in entities:
        x_pos = pos.get(entity['name'], (0, 0))[0]
        entity_positions.append((entity, x_pos))
    
    entity_positions.sort(key=lambda x: x[1])
    
    return [entity for entity, _ in entity_positions]

def find_optimal_ordering_inheritance_aware(entities: List[Dict[str, Any]], relationships: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
    """
    Find optimal entity ordering that prioritizes inheritance relationships.
    
    Places parent classes before children and groups inheritance hierarchies.
    """
    # Build inheritance graph
    inheritance_map = {}  # child -> parent
    children_map = defaultdict(list)  # parent -> [children]
    
    for entity in entities:
        if 'inherits' in entity:
            parent_class = entity['inherits']
            child_class = entity['class']
            inheritance_map[child_class] = parent_class
            children_map[parent_class].append(child_class)
    
    # Find root classes (no inheritance)
    roots = []
    non_roots = []
    
    for entity in entities:
        if entity['class'] not in inheritance_map:
            # This is either a root class or standalone entity
            if entity['class'] in children_map:
                roots.append(entity)  # Has children, so it's a root
            else:
                non_roots.append(entity)  # Standalone entity
        else:
            non_roots.append(entity)  # Child entity
    
    # Order roots by importance
    analysis = analyze_entity_relationships(entities, relationships)
    
    def get_entity_importance(entity):
        return calculate_entity_importance(entity, analysis)
    
    roots.sort(key=get_entity_importance, reverse=True)
    
    # For each root, do a depth-first traversal of its inheritance tree
    ordered_entities = []
    processed = set()
    
    def add_inheritance_tree(entity_class):
        if entity_class in processed:
            return
        
        # Find the entity object
        entity_obj = None
        for entity in entities:
            if entity['class'] == entity_class:
                entity_obj = entity
                break
        
        if entity_obj:
            ordered_entities.append(entity_obj)
            processed.add(entity_class)
            
            # Add children
            children = children_map.get(entity_class, [])
            # Sort children by importance
            child_entities = [e for e in entities if e['class'] in children]
            child_entities.sort(key=get_entity_importance, reverse=True)
            
            for child_entity in child_entities:
                add_inheritance_tree(child_entity['class'])
    
    # Process inheritance trees
    for root in roots:
        add_inheritance_tree(root['class'])
    
    # Add remaining non-root entities that aren't part of inheritance trees
    remaining = [e for e in non_roots if e['class'] not in processed]
    remaining.sort(key=get_entity_importance, reverse=True)
    ordered_entities.extend(remaining)
    
    return ordered_entities

def find_optimal_ordering_user_centric(entities: List[Dict[str, Any]], relationships: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
    """
    Find optimal entity ordering with User at the center and related entities grouped around it.
    
    This approach puts the most important business entities first.
    """
    # Find user entity
    user_entity = None
    user_related = []
    other_entities = []
    
    for entity in entities:
        if 'user' in entity['name'].lower():
            if entity['name'] == 'user':
                user_entity = entity
            else:
                user_related.append(entity)
        else:
            other_entities.append(entity)
    
    # Analyze relationships to find entities directly connected to user
    user_connections = set()
    for rel in relationships:
        if rel['from'] == 'user':
            user_connections.add(rel['to'])
        elif rel['to'] == 'user':
            user_connections.add(rel['from'])
    
    # Separate entities by their relationship to user
    directly_connected = []
    indirectly_connected = []
    standalone = []
    
    analysis = analyze_entity_relationships(entities, relationships)
    
    for entity in other_entities:
        entity_name = entity['name']
        if entity_name in user_connections:
            directly_connected.append(entity)
        elif entity_name in analysis['graph'] and nx.has_path(analysis['graph'], 'user', entity_name):
            indirectly_connected.append(entity)
        else:
            standalone.append(entity)
    
    # Sort each group by importance
    def get_entity_importance(entity):
        return calculate_entity_importance(entity, analysis)
    
    directly_connected.sort(key=get_entity_importance, reverse=True)
    indirectly_connected.sort(key=get_entity_importance, reverse=True)
    user_related.sort(key=get_entity_importance, reverse=True)
    standalone.sort(key=get_entity_importance, reverse=True)
    
    # Build final ordering: User -> User-related -> Directly connected -> Indirectly connected -> Standalone
    ordered_entities = []
    
    if user_entity:
        ordered_entities.append(user_entity)
    
    ordered_entities.extend(user_related)
    ordered_entities.extend(directly_connected)
    ordered_entities.extend(indirectly_connected)
    ordered_entities.extend(standalone)
    
    return ordered_entities

def compare_orderings(entities: List[Dict[str, Any]], relationships: List[Dict[str, Any]]) -> Dict[str, Any]:
    """
    Compare different ordering strategies and provide recommendations.
    """
    current_order = sorted(entities, key=lambda p: p["class"], reverse=True)
    
    orderings = {
        'current': current_order,
        'hierarchical': find_optimal_ordering_hierarchical(entities, relationships),
        'force_directed': find_optimal_ordering_force_directed(entities, relationships),
        'inheritance_aware': find_optimal_ordering_inheritance_aware(entities, relationships),
        'user_centric': find_optimal_ordering_user_centric(entities, relationships)
    }
    
    analysis = analyze_entity_relationships(entities, relationships)
    
    def calculate_ordering_score(ordering):
        """Calculate a quality score for an ordering (lower is better)."""
        score = 0.0
        entity_positions = {entity['name']: i for i, entity in enumerate(ordering)}
        
        # Penalize distance between related entities
        for rel in relationships:
            from_pos = entity_positions.get(rel['from'], 0)
            to_pos = entity_positions.get(rel['to'], 0)
            distance = abs(from_pos - to_pos)
            score += distance * distance  # Quadratic penalty for distance
        
        # Penalize inheritance separation
        for entity in ordering:
            if 'inherits' in entity:
                child_pos = entity_positions[entity['name']]
                parent_name = None
                # Find parent entity name
                for e in entities:
                    if e['class'] == entity['inherits']:
                        parent_name = e['name']
                        break
                
                if parent_name and parent_name in entity_positions:
                    parent_pos = entity_positions[parent_name]
                    if parent_pos > child_pos:  # Parent should come before child
                        score += 100  # Heavy penalty for wrong inheritance order
                    score += abs(parent_pos - child_pos)  # Distance penalty
        
        return score
    
    scores = {}
    for name, ordering in orderings.items():
        scores[name] = calculate_ordering_score(ordering)
    
    # Find best ordering
    best_ordering = min(orderings.items(), key=lambda x: scores[x[0]])
    
    return {
        'orderings': orderings,
        'scores': scores,
        'best': best_ordering,
        'analysis': analysis,
        'recommendations': generate_recommendations(orderings, scores, analysis)
    }

def generate_recommendations(orderings: Dict[str, List], scores: Dict[str, float], analysis: Dict[str, Any]) -> List[str]:
    """Generate human-readable recommendations based on the analysis."""
    recommendations = []
    
    best_name = min(scores.items(), key=lambda x: x[1])[0]
    worst_name = max(scores.items(), key=lambda x: x[1])[0]
    current_score = scores['current']
    best_score = scores[best_name]
    
    improvement = ((current_score - best_score) / current_score) * 100 if current_score > 0 else 0
    
    recommendations.append(f"Current ordering scores {current_score:.1f} (lower is better)")
    recommendations.append(f"Best ordering is '{best_name}' with score {best_score:.1f}")
    recommendations.append(f"Switching to '{best_name}' could improve diagram layout by {improvement:.1f}%")
    
    if analysis['num_components'] > 1:
        recommendations.append(f"Detected {analysis['num_components']} disconnected groups of entities")
        recommendations.append("Consider grouping related entities together in the diagram")
    
    # Check for inheritance issues
    inheritance_entities = [e for e in orderings['current'] if 'inherits' in e]
    if inheritance_entities:
        recommendations.append(f"Found {len(inheritance_entities)} entities with inheritance")
        recommendations.append("Consider using inheritance-aware ordering to keep parent-child entities close")
    
    # Check for high-centrality entities
    high_centrality = [name for name, centrality in analysis['degree_centrality'].items() if centrality > 0.3]
    if high_centrality:
        recommendations.append(f"High-connectivity entities: {', '.join(high_centrality)}")
        recommendations.append("These should be placed centrally to minimize line crossings")
    
    return recommendations

if __name__ == "__main__":
    # This would be called from the main gen.py script
    from parse_java import parse_entities
    
    entities, relationships = parse_entities()
    comparison = compare_orderings(entities, relationships)
    
    print("=== ENTITY ORDERING ANALYSIS ===\n")
    
    for rec in comparison['recommendations']:
        print(f"• {rec}")
    
    print(f"\n=== ORDERING SCORES ===")
    for name, score in comparison['scores'].items():
        print(f"{name:20}: {score:8.1f}")
    
    print(f"\n=== BEST ORDERING ===")
    best_ordering = comparison['best'][1]
    for i, entity in enumerate(best_ordering):
        print(f"{i+1:2}. {entity['name']:20} ({entity['class']})")
