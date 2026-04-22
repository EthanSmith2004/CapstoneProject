#!/usr/bin/env python3
from parse_java import parse_entities

entities, relationships = parse_entities()

print("=== ENTITIES ===")
for entity in entities:
    if 'transaction_audit' in entity['name'].lower():
        print(f"Entity: {entity['name']} ({entity['class']})")
        print(f"  Foreign keys: {entity['foreign_keys']}")

print("\n=== RELATIONSHIPS ===")
transaction_audit_relations = [r for r in relationships if 'transaction_audit' in r['from'].lower()]
print("TransactionAudit relationships:")
for rel in transaction_audit_relations:
    print(f"  {rel['from']} -> {rel['to']} ({rel['cardinality']})")

print("\nUser relationships:")
user_relations = [r for r in relationships if 'user' in r['to'].lower()]
for rel in user_relations:
    print(f"  {rel['from']} -> {rel['to']} ({rel['cardinality']})")
