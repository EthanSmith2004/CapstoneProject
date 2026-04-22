#!/usr/bin/env python3
"""
Generate both ERD and Class diagrams from Java source code.
"""
import os
import sys
from gen import main as gen_main

def generate_all_diagrams():
    """Generate both ERD and class diagrams."""
    script_dir = os.path.dirname(os.path.abspath(__file__))
    docs_dir = os.path.join(os.path.dirname(script_dir), 'docs')
    
    # Ensure docs directory exists
    if not os.path.exists(docs_dir):
        os.makedirs(docs_dir)
    
    # Generate ERD
    print("Generating ERD...")
    sys.argv = ['generate_all.py', 'erd', '--output', os.path.join(docs_dir, 'erd.plantuml')]
    gen_main()
    
    # Generate Class Diagram
    print("Generating Class Diagram...")
    sys.argv = ['generate_all.py', 'class_diagram', '--output', os.path.join(docs_dir, 'class_diagram.plantuml')]
    gen_main()
      print("\nAll diagrams generated successfully!")
    print(f"ERD: {os.path.join(docs_dir, 'erd.plantuml')}")
    print(f"Class Diagram: {os.path.join(docs_dir, 'class_diagram.plantuml')}")
    print(f"Individual Entity Diagrams: {os.path.join(docs_dir, 'entities', '*.plantuml')}")
    
    print("\nTo view the diagrams:")
    print("1. Install PlantUML: https://plantuml.com/")
    print("2. Or use online viewer: http://www.plantuml.com/plantuml/uml/")
    print("3. Or use VS Code PlantUML extension")

if __name__ == "__main__":
    generate_all_diagrams()
