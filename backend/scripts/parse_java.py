import os
import re
from typing import List, Dict, Any, Tuple

def parse_entities(entity_path: str = None) -> Tuple[List[Dict[str, Any]], List[Dict[str, Any]]]:
    """
    Parse Java entity files to extract entity and relationship information.
    
    Returns:
        Tuple of (entities, relationships) where:
        - entities: List of entity dictionaries with name, class, primary_key, columns, foreign_keys
        - relationships: List of relationship dictionaries with from, to, cardinality
    """
    if entity_path is None:
        # Get the absolute path to the entity directory
        script_dir = os.path.dirname(os.path.abspath(__file__))
        backend_dir = os.path.dirname(script_dir)
        entity_path = os.path.join(backend_dir, 'src', 'main', 'java', 'com', 'jel', 'spys', 'entity')
    
    entities = []
    relationships = []
    
    # First pass: collect all entities
    entity_files = [f for f in os.listdir(entity_path) if f.endswith('.java')]
    
    for file_name in entity_files:
        file_path = os.path.join(entity_path, file_name)
        
        try:
            with open(file_path, 'r', encoding='utf-8') as file:
                content = file.read()
                
            # Skip non-entity files (enums, abstract classes without @Entity or @MappedSuperclass)
            if '@Entity' not in content and '@MappedSuperclass' not in content:
                continue
                
            entity_info = parse_single_entity(content, file_name)
            if entity_info:
                entities.append(entity_info)
                
        except Exception as e:
            print(f"Error parsing {file_name}: {e}")
    
    # Second pass: extract relationships
    for file_name in entity_files:
        file_path = os.path.join(entity_path, file_name)
        
        try:
            with open(file_path, 'r', encoding='utf-8') as file:
                content = file.read()
                
            if '@Entity' not in content and '@MappedSuperclass' not in content:
                continue
                
            entity_relationships = parse_entity_relationships(content, file_name, entities)
            relationships.extend(entity_relationships)
                
        except Exception as e:
            print(f"Error parsing relationships in {file_name}: {e}")
    
    return entities, relationships

def parse_classes(base_path: str = None) -> List[Dict[str, Any]]:
    """
    Parse Java classes from controllers, services, facades, and security packages.
    
    Returns:
        List of class dictionaries with name, type, fields, and methods
    """
    if base_path is None:
        # Get the absolute path to the java source directory
        script_dir = os.path.dirname(os.path.abspath(__file__))
        backend_dir = os.path.dirname(script_dir)
        base_path = os.path.join(backend_dir, 'src', 'main', 'java', 'com', 'jel', 'spys')
    
    classes = []
    
    # Define the packages to parse and their types
    packages_to_parse = {
        'controllers': 'controller',
        'service': 'service', 
        'facade': 'facade',
        'security': 'security'
    }
    
    for package_name, class_type in packages_to_parse.items():
        package_path = os.path.join(base_path, package_name)
        
        if not os.path.exists(package_path):
            continue
            
        try:
            java_files = [f for f in os.listdir(package_path) if f.endswith('.java')]
            
            for file_name in java_files:
                file_path = os.path.join(package_path, file_name)
                
                try:
                    with open(file_path, 'r', encoding='utf-8') as file:
                        content = file.read()
                    
                    class_info = parse_single_class(content, file_name, class_type)
                    if class_info:
                        classes.append(class_info)
                        
                except Exception as e:
                    print(f"Error parsing {file_name}: {e}")
                    
        except Exception as e:
            print(f"Error accessing package {package_name}: {e}")
    
    return classes

def parse_single_class(content: str, file_name: str, class_type: str) -> Dict[str, Any]:
    """Parse a single Java class file and extract class information."""
    
    # Extract class name
    class_match = re.search(r'(?:public\s+)?(?:abstract\s+)?class\s+(\w+)', content)
    if not class_match:
        return None
    
    class_name = class_match.group(1)
    
    # Extract fields
    fields = []
    field_pattern = r'((?:@[^\n]*(?:\n\s*)?)*)\s*(private|protected|public)\s+(?:static\s+)?(?:final\s+)?(\w+(?:<[^>]+>)?)\s+(\w+)\s*(?:=.*?)?;'
    field_matches = re.finditer(field_pattern, content, re.MULTILINE | re.DOTALL)
    
    for match in field_matches:
        annotations = match.group(1) if match.group(1) else ""
        visibility = match.group(2)
        field_type = match.group(3)
        field_name = match.group(4)
        
        # Skip static final constants (often used for logging)
        if 'static final' in match.group(0) or 'final static' in match.group(0):
            continue
            
        fields.append({
            "name": field_name,
            "visibility": visibility,
            "type": field_type
        })
    
    # Extract methods (including constructors)
    methods = []
    
    # Pattern for constructors
    ctor_pattern = rf'((?:@[^\n]*(?:\n\s*)?)*)\s*(public|protected|private)\s+{re.escape(class_name)}\s*\(([^)]*)\)\s*(?:throws\s+[^{{]+)?{{'
    ctor_matches = re.finditer(ctor_pattern, content, re.MULTILINE | re.DOTALL)
    
    for match in ctor_matches:
        annotations = match.group(1) if match.group(1) else ""
        visibility = match.group(2)
        params = match.group(3).strip()
        
        # Parse parameters
        param_list = []
        if params:
            # Split by comma, but handle generics
            param_parts = re.split(r',(?![^<>]*>)', params)
            for param in param_parts:
                param = param.strip()
                if param:
                    # Extract parameter name (last word)
                    param_words = param.split()
                    if len(param_words) >= 2:
                        param_list.append(param_words[-1])
        
        method_signature = f"{class_name}({', '.join(param_list)})"
        methods.append({
            "name": method_signature,
            "visibility": visibility
        })    # Pattern for regular methods - improved to handle multi-line methods
    # This pattern handles methods that may span multiple lines due to annotations and parameters
    method_pattern = r'((?:@[^\n]*(?:\n\s*)?)*)\s*(public|protected|private)\s+(?:static\s+)?(?:final\s+)?(?:synchronized\s+)?([^\s]+)\s+(\w+)\s*\(([^)]*)\)[^{]*\{'
    method_matches = re.finditer(method_pattern, content, re.MULTILINE | re.DOTALL)
    
    for match in method_matches:
        annotations = match.group(1) if match.group(1) else ""
        visibility = match.group(2)
        return_type = match.group(3)
        method_name = match.group(4)
        params_block = match.group(5).strip()
        
        # Parse parameters - handle multi-line parameter blocks
        param_list = []
        if params_block:
            # Remove newlines and extra whitespace, but preserve parameter structure
            params_clean = re.sub(r'\s+', ' ', params_block)
            
            # Split by comma, but handle generics and nested annotations
            param_parts = []
            bracket_depth = 0
            angle_depth = 0
            current_param = ""
            
            for char in params_clean:
                if char == '<':
                    angle_depth += 1
                elif char == '>':
                    angle_depth -= 1
                elif char == '(':
                    bracket_depth += 1                elif char == ')':
                    bracket_depth -= 1
                elif char == ',' and bracket_depth == 0 and angle_depth == 0:
                    param_parts.append(current_param.strip())
                    current_param = ""
                    continue
                current_param += char
            
            if current_param.strip():
                param_parts.append(current_param.strip())
            
            for param in param_parts:
                param = param.strip()
                if param:
                    # Remove annotations like @RequestParam
                    param_clean = re.sub(r'@\w+(?:\([^)]*\))?\s*', '', param)
                    # Extract parameter name (last word that's not a keyword)
                    param_words = param_clean.split()
                    if len(param_words) >= 2:
                        # Get the last word as parameter name
                        param_name = param_words[-1]
                        # Remove array brackets if present
                        param_name = param_name.replace('[]', '')
                        # Remove default values that might be attached
                        param_name = re.sub(r'["\'].*["\']', '', param_name)
                        param_name = param_name.strip()
                        if param_name:
                            param_list.append(param_name)
        
        method_signature = f"{method_name}({', '.join(param_list)})"
        methods.append({
            "name": method_signature,
            "visibility": visibility
        })
    
    return {
        "name": class_name,
        "type": class_type,
        "fields": fields,
        "methods": methods
    }

def parse_single_entity(content: str, file_name: str) -> Dict[str, Any]:
    """Parse a single entity file and extract entity information."""
    
    # Check if this is a MappedSuperclass
    is_mapped_superclass = '@MappedSuperclass' in content
    
    # Extract class name and inheritance
    class_match = re.search(r'public\s+(?:abstract\s+)?class\s+(\w+)(?:\s+extends\s+(\w+))?', content)
    if not class_match:
        return None
    
    class_name = class_match.group(1)
    parent_class = class_match.group(2) if class_match.group(2) else None
    
    # Extract table name from @Table annotation
    table_match = re.search(r'@Table\s*\(\s*name\s*=\s*["\'](\w+)["\']', content)
    entity_name = table_match.group(1) if table_match else class_name.replace('Entity', '').lower()
    
    # Extract primary key fields - look for @Id annotation followed by field declaration
    primary_keys = []
    
    # More precise pattern for @Id fields, including final fields
    id_pattern = r'@Id\s*(?:\n\s*@[^\n]*)*\s*(?:\n\s*)*(?:private|protected|public)\s+(?:static\s+)?(?:final\s+)?\w+\s+(\w+)\s*(?:=.*?)?;'
    id_matches = re.finditer(id_pattern, content, re.MULTILINE | re.DOTALL)
    for match in id_matches:
        primary_keys.append(match.group(1))
    
    # If no @Id found, look for fields named 'id'
    if not primary_keys:
        id_field_match = re.search(r'(?:private|protected|public)\s+\w+\s+(id)\s*;', content)
        if id_field_match:
            primary_keys.append('id')
    
    # Extract columns and foreign keys
    columns = set()
    foreign_keys = []
    
    # Find field declarations with a more precise pattern
    # Match from @ annotations through the field declaration, including final fields
    field_pattern = r'((?:@[^\n]*(?:\n\s*)?)*)\s*(private|protected|public)\s+(?:static\s+)?(?:final\s+)?(\w+(?:<[^>]+>)?)\s+(\w+)\s*(?:=.*?)?;'
    field_matches = re.finditer(field_pattern, content, re.MULTILINE | re.DOTALL)
    
    for match in field_matches:
        annotations = match.group(1) if match.group(1) else ""
        visibility = match.group(2)
        field_type = match.group(3)
        field_name = match.group(4)
        
        # Skip relationship fields
        if any(annotation in annotations for annotation in ['@OneToMany', '@ManyToOne', '@OneToOne', '@ManyToMany']):
            # Check if it's a foreign key relationship
            if '@ManyToOne' in annotations or ('@OneToOne' in annotations and '@JoinColumn' in annotations):
                # Extract column name from @JoinColumn if present
                join_col_match = re.search(r'@JoinColumn\s*\([^)]*name\s*=\s*["\'](\w+)["\']', annotations)
                if join_col_match:
                    fk_column = join_col_match.group(1)
                    foreign_keys.append(fk_column)
                else:
                    # Default foreign key naming convention
                    fk_column = f"{field_name}_id"
                    foreign_keys.append(fk_column)
            continue
            
        # Skip collection fields that are not mapped columns
        if any(collection_type in field_type for collection_type in ['List<', 'Set<', 'Collection<']):
            continue
        
        # Skip enum collections
        if '@ElementCollection' in annotations:
            continue
        
        # Skip method-like patterns (shouldn't match anyway with this pattern, but just in case)
        if '(' in field_name or ')' in field_name:
            continue
            
        # Determine the actual column name
        column_name = field_name
        
        # Check for @Column annotation with specific name
        column_match = re.search(r'@Column\s*\([^)]*name\s*=\s*["\'](\w+)["\']', annotations)
        if column_match:
            column_name = column_match.group(1)
        
        columns.add(column_name)
    
    # Build the entity dictionary
    entity_dict = {
        "name": entity_name,
        "class": class_name,
        "primary_key": primary_keys,
        "columns": sorted(list(columns)),  # Convert set to sorted list
        "foreign_keys": foreign_keys,
        "is_mapped_superclass": is_mapped_superclass
    }
    
    # Add inheritance information if present
    if parent_class:
        entity_dict["inherits"] = parent_class
    
    return entity_dict

def parse_entity_relationships(content: str, file_name: str, entities: List[Dict]) -> List[Dict[str, Any]]:
    """Parse relationships from an entity file."""
    relationships = []
    
    # Extract class name
    class_match = re.search(r'public\s+(?:abstract\s+)?class\s+(\w+)', content)
    if not class_match:
        return relationships
    
    current_class = class_match.group(1)
    current_entity_name = None
    
    # Find current entity name
    for entity in entities:
        if entity['class'] == current_class:
            current_entity_name = entity['name']
            break
    
    if not current_entity_name:
        return relationships
    
    # Find relationship annotations
    relationship_patterns = [
        (r'@OneToMany[^;]*private\s+(?:final\s+)?List<(\w+)>', "1..1", "1..*"),
        (r'@ManyToOne[^;]*private\s+(?:final\s+)?(\w+)', "1..*", "1..1"),
        (r'@OneToOne[^;]*private\s+(?:final\s+)?(\w+)', "1..1", "1..1"),
        (r'@ManyToMany[^;]*private\s+(?:final\s+)?List<(\w+)>', "1..*", "1..*")
    ]
    
    for pattern, from_card, to_card in relationship_patterns:
        matches = re.finditer(pattern, content, re.MULTILINE | re.DOTALL)
        for match in matches:
            # Get the full match to check for mappedBy
            full_match = match.group(0)
            
            # Skip inverse relationships (mappedBy)
            if 'mappedBy' in full_match:
                continue
                
            related_class = match.group(1)
            
            # Find the entity name for the related class
            related_entity_name = None
            for entity in entities:
                if entity['class'] == related_class:
                    related_entity_name = entity['name']
                    break
            
            if related_entity_name:
                relationships.append({
                    "from": current_entity_name,
                    "to": related_entity_name,
                    "cardinality": [from_card, to_card]
                })
    
    return relationships

def format_output_as_python(entities: List[Dict[str, Any]], relationships: List[Dict[str, Any]], classes: List[Dict[str, Any]] = None) -> str:
    """Format the entities, relationships, and classes as clean Python code."""
    
    output = "# Parsed Java entities\n"
    output += "entities = [\n"
    
    for entity in entities:
        output += f'    {{"name": "{entity["name"]}", "class": "{entity["class"]}", '
        output += f'"primary_key": {entity["primary_key"]}, '
        output += f'"columns": {entity["columns"]}, '
        output += f'"foreign_keys": {entity["foreign_keys"]}'
        
        # Add inherits field if present
        if "inherits" in entity:
            output += f', "inherits": "{entity["inherits"]}"'
        
        output += '},\n'
    
    output += "]\n\n"
    output += "relationships = [\n"
    
    for rel in relationships:
        output += f'    {{"from": "{rel["from"]}", "to": "{rel["to"]}", '
        output += f'"cardinality": {rel["cardinality"]}}},\n'
    
    output += "]\n"
    
    # Add classes if provided
    if classes:
        output += "\nclasses = [\n"
        
        for cls in classes:
            output += f'    {{"name": "{cls["name"]}", "type": "{cls["type"]}", '
            output += f'"fields": {cls["fields"]}, '
            output += f'"methods": {cls["methods"]}}},\n'
        
        output += "]\n"
    
    return output

def parse_java_files():
    """Main function to parse all Java entity files and classes."""
    entities, relationships = parse_entities()
    classes = parse_classes()
    
    print("=== PARSED ENTITIES, RELATIONSHIPS, AND CLASSES ===\n")
    
    # Print formatted Python code
    formatted_output = format_output_as_python(entities, relationships, classes)
    print(formatted_output)
    
    print("\n=== SUMMARY ===")
    print(f"Found {len(entities)} entities, {len(relationships)} relationships, and {len(classes)} classes")
    
    return entities, relationships, classes

if __name__ == "__main__":
    parse_java_files()
