import argparse
import os
import subprocess
import json
import jinja2
from entity_optimizer import (
    find_optimal_ordering_force_directed,
    find_optimal_ordering_hierarchical,
    find_optimal_ordering_inheritance_aware,
    find_optimal_ordering_user_centric
)

def extract_classes_from_gradle():
    """Extract class information using the Gradle extractClasses task."""
    try:
        # Get the backend directory (parent of scripts directory)
        script_dir = os.path.dirname(os.path.abspath(__file__))
        backend_dir = os.path.dirname(script_dir)
        
        # Run the Gradle task
        result = subprocess.run(
            [os.path.join(backend_dir, 'gradlew.bat'), 'extractClasses'],
            cwd=backend_dir,
            capture_output=True,
            text=True,
            check=True
        )
          # Parse the JSON output (extract JSON from Gradle output)
        output_lines = result.stdout.strip().split('\n')
        json_output = None
        
        # Find the line that starts with '{'
        for line in output_lines:
            line = line.strip()
            if line.startswith('{'):
                json_output = line
                break
        
        if not json_output:
            raise ValueError("No JSON output found in Gradle task output")
        
        class_data = json.loads(json_output)
        
        return class_data.get('classes', []), class_data.get('entities', []), class_data.get('relationships', [])
        
    except subprocess.CalledProcessError as e:
        print(f"Error running Gradle task: {e}")
        print(f"Stdout: {e.stdout}")
        print(f"Stderr: {e.stderr}")
        raise
    except json.JSONDecodeError as e:
        print(f"Error parsing JSON output: {e}")
        print(f"Raw output: {result.stdout}")
        raise

def get_template(diagram_type):
    template_file = f'{diagram_type}.plantuml.j2'
    
    template_path = os.path.join(os.path.dirname(__file__), 'template', template_file)
    if not os.path.exists(template_path):
        raise ValueError(f"Template for {diagram_type} not found at {template_path}")
    with open(template_path, 'r') as file:
        return file.read()

def main():
    parser = argparse.ArgumentParser(description="Generate a ClassDiagram or ERD from java files.")
    parser.add_argument('type', type=str, help='The type of the file to generate (erd or class_diagram)')
    parser.add_argument('--output', type=str, help='The path of the output file (default ./docs/{type}.{ext})')
    args = parser.parse_args()

    if not args.output:
        args.output = f'./docs/{args.type}.plantuml'
    else:
        args.output = os.path.abspath(args.output)    

    if not os.path.exists(os.path.dirname(args.output)):
        os.makedirs(os.path.dirname(args.output))

    # Extract class information using Gradle
    print("Extracting class information using Gradle...")
    classes, entities, relationships = extract_classes_from_gradle()
    
    print(f"Extracted {len(classes)} classes, {len(entities)} entities, {len(relationships)} relationships")    # Optimize entity ordering to prevent bunching
    if entities:
        entities = find_optimal_ordering_force_directed(entities, relationships)
    
    # Generate the main diagram
    with open(args.output, 'w', encoding='utf-8') as f:
        template = jinja2.Template(get_template(args.type))
        f.write(template.render(
            entities=entities, 
            relationships=relationships, 
            classes=classes,
            project_name="Backend Application",
            timestamp="2025-01-08T12:00:00.000Z"
        ))
    
    print(f"Generated {args.type} diagram at: {args.output}")

if __name__ == "__main__":
    main()
