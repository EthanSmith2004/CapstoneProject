
#!/usr/bin/env python3
"""
SVG Element Extractor

This script extracts individual groups from SVG files and saves them as separate SVG files
with tight bounding boxes using Inkscape command-line interface.

Usage:
    python extract_svg_files.py [input_file.svg] [--output-dir output_directory]
    
Features:
- Extracts individual groups from SVG files
- Applies tight bounding boxes to extracted elements
- Uses Inkscape for professional SVG processing
- Supports batch processing of multiple files
"""

import os
import sys
import subprocess
import argparse
import xml.etree.ElementTree as ET
import re
from pathlib import Path


class SVGExtractor:
    def __init__(self, inkscape_path=None):
        """
        Initialize the SVG extractor.
        
        Args:
            inkscape_path (str): Path to Inkscape executable. If None, tries to find it automatically.
        """
        self.inkscape_path = inkscape_path or self._find_inkscape()
        if not self.inkscape_path:
            raise RuntimeError("Inkscape not found. Please install Inkscape or provide the path.")
    
    def _find_inkscape(self):
        """Try to find Inkscape executable in common locations."""
        possible_paths = [
            "inkscape",  # If it's in PATH
            "C:\\Program Files\\Inkscape\\bin\\inkscape.exe",
            "C:\\Program Files (x86)\\Inkscape\\bin\\inkscape.exe",
            "/usr/bin/inkscape",
            "/usr/local/bin/inkscape",
            "/Applications/Inkscape.app/Contents/MacOS/inkscape"
        ]
        
        for path in possible_paths:
            try:
                subprocess.run([path, "--version"], 
                             capture_output=True, check=True, timeout=10)
                print(f"Found Inkscape at: {path}")
                return path
            except (subprocess.CalledProcessError, FileNotFoundError, subprocess.TimeoutExpired):
                continue
        
        return None
    
    def parse_svg_groups(self, svg_file):
        """
        Parse SVG file and extract information about groups.
        
        Args:
            svg_file (str): Path to SVG file
            
        Returns:
            list: List of dictionaries containing group information
        """
        try:
            tree = ET.parse(svg_file)
            root = tree.getroot()
            
            # Handle namespace
            namespace = {'svg': 'http://www.w3.org/2000/svg'}
            if root.tag.startswith('{'):
                namespace_uri = root.tag.split('}')[0][1:]
                namespace = {'svg': namespace_uri}
            
            groups = []
            
            # Find all groups with IDs
            for group in root.findall('.//svg:g[@id]', namespace):
                group_id = group.get('id')
                if group_id:
                    # Get bounding box if available
                    bbox = self._get_group_bbox(group)
                    groups.append({
                        'id': group_id,
                        'element': group,
                        'bbox': bbox
                    })
            
            # If no groups with IDs found, try to find groups by class or other attributes
            if not groups:
                for group in root.findall('.//svg:g', namespace):
                    # Generate ID based on content or position
                    group_id = self._generate_group_id(group)
                    if group_id:
                        bbox = self._get_group_bbox(group)
                        groups.append({
                            'id': group_id,
                            'element': group,
                            'bbox': bbox
                        })
            
            print(f"Found {len(groups)} groups in {svg_file}")
            return groups
            
        except ET.ParseError as e:
            print(f"Error parsing SVG file {svg_file}: {e}")
            return []
    
    def _get_group_bbox(self, group):
        """Extract bounding box from group element if available."""
        # This is a simplified version - Inkscape will handle the actual bbox calculation
        return None
    
    def _generate_group_id(self, group):
        """Generate an ID for a group that doesn't have one."""
        # Look for text content to generate meaningful names
        texts = []
        for text_elem in group.findall('.//'):
            if text_elem.text and text_elem.text.strip():
                texts.append(text_elem.text.strip())
        
        if texts:
            # Use the first text as ID, cleaned up
            text_id = texts[0]
            text_id = re.sub(r'[^a-zA-Z0-9_]', '_', text_id)
            text_id = re.sub(r'_+', '_', text_id)
            return f"group_{text_id[:20]}"
        
        return None
    
    def extract_group_with_inkscape(self, svg_file, group_id, output_file):
        """
        Extract a specific group using Inkscape command line with truly tight bounding box.
        
        Args:
            svg_file (str): Input SVG file
            group_id (str): ID of the group to extract
            output_file (str): Output SVG file path
            
        Returns:
            bool: True if successful, False otherwise
        """
        try:
            print(f"Extracting {group_id} to {output_file}")
            
            # Step 1: Export the specific group with ID-only
            temp_file = output_file + ".temp.svg"
            cmd_extract = [
                self.inkscape_path,
                svg_file,
                "--export-id", group_id,
                "--export-id-only",
                "--export-type=svg",
                "--export-filename", temp_file
            ]
            
            result = subprocess.run(cmd_extract, capture_output=True, text=True, timeout=60)
            if result.returncode != 0:
                print(f"✗ Failed to extract {group_id}: {result.stderr}")
                return False
            
            # Step 2: Use Inkscape actions to fit page to selection (this gives tight bounds)
            cmd_fit = [
                self.inkscape_path,
                temp_file,
                "--actions=select-all;fit-page-to-selection",
                "--export-type=svg",
                "--export-filename", output_file
            ]
            
            result = subprocess.run(cmd_fit, capture_output=True, text=True, timeout=60)
            
            # Clean up temp file
            try:
                os.remove(temp_file)
            except:
                pass
            
            if result.returncode == 0:
                print(f"✓ Successfully extracted {group_id} with tight bounds")
                return True
            else:
                print(f"✗ Failed to fit page for {group_id}: {result.stderr}")
                # If fit-page fails, try the old method as fallback
                return self._extract_fallback(svg_file, group_id, output_file)
                
        except subprocess.TimeoutExpired:
            print(f"✗ Timeout extracting {group_id}")
            return False
        except Exception as e:
            print(f"✗ Error extracting {group_id}: {e}")
            return False
    
    def _extract_fallback(self, svg_file, group_id, output_file):
        """Fallback extraction method using export-area-drawing"""
        try:
            cmd = [
                self.inkscape_path,
                svg_file,
                "--export-id", group_id,
                "--export-id-only",
                "--export-area-drawing",
                "--export-type=svg", 
                "--export-filename", output_file
            ]
            
            result = subprocess.run(cmd, capture_output=True, text=True, timeout=60)
            if result.returncode == 0:
                print(f"✓ Extracted {group_id} (fallback method)")
                return True
            else:
                print(f"✗ Fallback also failed for {group_id}")
                return False
        except Exception as e:
            print(f"✗ Fallback error for {group_id}: {e}")
            return False
    
    def extract_all_groups(self, svg_file, output_dir):
        """
        Extract all groups from an SVG file.
        
        Args:
            svg_file (str): Input SVG file
            output_dir (str): Output directory
            
        Returns:
            dict: Results of extraction process
        """
        # Ensure output directory exists
        Path(output_dir).mkdir(parents=True, exist_ok=True)
        
        # Parse groups
        groups = self.parse_svg_groups(svg_file)
        
        if not groups:
            print(f"No groups found in {svg_file}")
            return {'total': 0, 'successful': 0, 'failed': 0}
        
        successful = 0
        failed = 0
        
        # Extract each group
        for group in groups:
            group_id = group['id']
            output_file = os.path.join(output_dir, f"{group_id}.svg")
            
            if self.extract_group_with_inkscape(svg_file, group_id, output_file):
                successful += 1
            else:
                failed += 1
        
        print(f"\nExtraction complete:")
        print(f"  Total groups: {len(groups)}")
        print(f"  Successful: {successful}")
        print(f"  Failed: {failed}")
        
        return {
            'total': len(groups),
            'successful': successful,
            'failed': failed,
            'groups': [g['id'] for g in groups]
        }
    
    def batch_extract(self, svg_files, output_base_dir):
        """
        Extract groups from multiple SVG files.
        
        Args:
            svg_files (list): List of SVG file paths
            output_base_dir (str): Base output directory
            
        Returns:
            dict: Overall results
        """
        overall_results = {
            'files_processed': 0,
            'total_groups': 0,
            'total_successful': 0,
            'total_failed': 0,
            'file_results': {}
        }
        
        for svg_file in svg_files:
            if not os.path.exists(svg_file):
                print(f"⚠ File not found: {svg_file}")
                continue
            
            print(f"\n{'='*60}")
            print(f"Processing: {svg_file}")
            print(f"{'='*60}")
            
            # Create output directory for this file
            file_name = Path(svg_file).stem
            output_dir = os.path.join(output_base_dir, file_name)
            
            # Extract groups
            results = self.extract_all_groups(svg_file, output_dir)
            
            # Update overall results
            overall_results['files_processed'] += 1
            overall_results['total_groups'] += results['total']
            overall_results['total_successful'] += results['successful']
            overall_results['total_failed'] += results['failed']
            overall_results['file_results'][svg_file] = results
        
        return overall_results


def main():
    parser = argparse.ArgumentParser(description='Extract SVG groups with tight bounding boxes using Inkscape')
    parser.add_argument('input_files', nargs='*', help='Input SVG files (if none specified, uses default files)')
    parser.add_argument('--output-dir', '-o', default='extracted_elements', 
                       help='Output directory (default: extracted_elements)')
    parser.add_argument('--inkscape-path', help='Path to Inkscape executable')
    parser.add_argument('--list-groups', action='store_true', 
                       help='Only list groups without extracting')
    
    args = parser.parse_args()
    
    # Default files if none specified
    if not args.input_files:
        script_dir = Path(__file__).parent
        docs_dir = script_dir.parent / 'docs'
        args.input_files = [
            str(docs_dir / 'ERDEntities.svg'),
            str(docs_dir / 'CDClasses.svg')
        ]
        print(f"Using default files: {args.input_files}")
    
    try:
        # Initialize extractor
        extractor = SVGExtractor(args.inkscape_path)
        
        if args.list_groups:
            # Just list groups
            for svg_file in args.input_files:
                if os.path.exists(svg_file):
                    print(f"\nGroups in {svg_file}:")
                    groups = extractor.parse_svg_groups(svg_file)
                    for group in groups:
                        print(f"  - {group['id']}")
                else:
                    print(f"File not found: {svg_file}")
        else:
            # Extract groups
            results = extractor.batch_extract(args.input_files, args.output_dir)
            
            print(f"\n{'='*60}")
            print("OVERALL RESULTS")
            print(f"{'='*60}")
            print(f"Files processed: {results['files_processed']}")
            print(f"Total groups: {results['total_groups']}")
            print(f"Successful extractions: {results['total_successful']}")
            print(f"Failed extractions: {results['total_failed']}")
            
            if results['total_groups'] > 0:
                success_rate = (results['total_successful'] / results['total_groups']) * 100
                print(f"Success rate: {success_rate:.1f}%")
            
            print(f"\nExtracted files saved to: {args.output_dir}")
    
    except Exception as e:
        print(f"Error: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()