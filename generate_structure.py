import os
import re
from pathlib import Path

def extract_kotlin_signatures(file_path):
    """Extract function/method signatures from Kotlin files"""
    signatures = []
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()

        # Match class declarations
        class_pattern = r'(?:open |abstract |sealed |data |inner |enum )?class\s+(\w+)(?:<[^>]+>)?\s*(?:\([^)]*\))?\s*(?::\s*[^{]+)?'
        for match in re.finditer(class_pattern, content):
            signatures.append(f"class {match.group(1)}")

        # Match object declarations
        object_pattern = r'object\s+(\w+)'
        for match in re.finditer(object_pattern, content):
            signatures.append(f"object {match.group(1)}")

        # Match function declarations
        func_pattern = r'(?:override\s+)?(?:private\s+|protected\s+|public\s+|internal\s+)?(?:suspend\s+)?fun\s+(?:<[^>]+>\s+)?(\w+)\s*\(([^)]*)\)\s*(?::\s*([^{=]+))?'
        for match in re.finditer(func_pattern, content):
            func_name = match.group(1)
            params = match.group(2).strip() if match.group(2) else ""
            return_type = match.group(3).strip() if match.group(3) else ""

            # Clean up parameters
            if params:
                param_list = []
                for param in params.split(','):
                    param = param.strip()
                    if param:
                        # Extract just name and type
                        param_parts = param.split(':')
                        if len(param_parts) >= 2:
                            param_name = param_parts[0].strip()
                            param_type = ':'.join(param_parts[1:]).strip()
                            # Remove default values
                            param_type = re.sub(r'=.*$', '', param_type).strip()
                            param_list.append(f"{param_name}: {param_type}")
                        else:
                            param_list.append(param)
                params = ", ".join(param_list)

            sig = f"fun {func_name}({params})"
            if return_type:
                sig += f": {return_type}"
            signatures.append(sig)

    except Exception as e:
        signatures.append(f"Error reading file: {str(e)}")

    return signatures

def extract_java_signatures(file_path):
    """Extract function/method signatures from Java files"""
    signatures = []
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()

        # Match class declarations
        class_pattern = r'(?:public |private |protected )?(?:static |final |abstract )*class\s+(\w+)(?:<[^>]+>)?\s*(?:extends\s+\w+(?:<[^>]+>)?)?\s*(?:implements\s+[^{]+)?'
        for match in re.finditer(class_pattern, content):
            signatures.append(f"class {match.group(1)}")

        # Match method declarations
        method_pattern = r'(?:public |private |protected )?(?:static |final |abstract |synchronized )*(?:<[^>]+>\s+)?(\w+(?:<[^>]+>)?)\s+(\w+)\s*\(([^)]*)\)'
        for match in re.finditer(method_pattern, content):
            return_type = match.group(1)
            method_name = match.group(2)
            params = match.group(3).strip() if match.group(3) else ""

            # Skip constructors and common non-methods
            if return_type in ['class', 'interface', 'enum', 'import', 'package']:
                continue

            sig = f"{return_type} {method_name}({params})"
            signatures.append(sig)

    except Exception as e:
        signatures.append(f"Error reading file: {str(e)}")

    return signatures

def generate_project_structure(root_path, output_file):
    """Generate project structure and signatures"""
    root = Path(root_path)

    print(f"Looking in: {root.absolute()}")
    print(f"Checking for src/main/kotlin...")
    print(f"Exists: {(root / 'src' / 'main' / 'kotlin').exists()}")
    print(f"Checking for src/main/java...")
    print(f"Exists: {(root / 'src' / 'main' / 'java').exists()}")

    with open(output_file, 'w', encoding='utf-8') as out:
        total_files = 0
        total_sigs = 0
        total_unique_sigs = 0

        # Walk through src/main/java and src/main/kotlin
        for src_type in ['java', 'kotlin']:
            src_path = root / 'src' / 'main' / src_type

            if not src_path.exists():
                print(f"Path does not exist: {src_path}")
                continue

            # Get all files sorted
            if src_type == 'kotlin':
                files = sorted(src_path.rglob('*.kt'))
            else:
                files = sorted(src_path.rglob('*.java'))

            print(f"Found {len(files)} {src_type} files")
            total_files += len(files)

            for file_path in files:
                # Get relative path from src root
                rel_path = file_path.relative_to(src_path)

                out.write(f"\n{rel_path}\n")

                # Extract signatures
                if src_type == 'kotlin':
                    signatures = extract_kotlin_signatures(file_path)
                else:
                    signatures = extract_java_signatures(file_path)

                # Remove duplicates while preserving order
                seen = set()
                unique_sigs = []
                for sig in signatures:
                    if sig not in seen:
                        seen.add(sig)
                        unique_sigs.append(sig)

                total_sigs += len(signatures)
                total_unique_sigs += len(unique_sigs)

                if unique_sigs:
                    for sig in unique_sigs:
                        out.write(f"  {sig}\n")
                else:
                    out.write("  (no signatures found)\n")

        print(f"\nTotal files processed: {total_files}")
        print(f"Total signatures: {total_sigs}")
        print(f"Unique signatures: {total_unique_sigs}")
        print(f"Duplicates removed: {total_sigs - total_unique_sigs}")

if __name__ == "__main__":
    # Set your project root path here
    project_root = "."  # Current directory
    output_file = "project_structure.txt"

    print(f"Current working directory: {os.getcwd()}")
    print(f"Generating project structure from: {project_root}")
    print(f"Output file: {output_file}")
    print("-" * 80)

    generate_project_structure(project_root, output_file)

    print("-" * 80)
    print(f"Done! Check {output_file}")
