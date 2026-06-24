# STATE

- **Current Branch**: scarpet-imports (from `da1e1a28`)
- **Last Commit**: `f2540102` Implement new import syntax and lexical scoping cache
- **Completed Tasks**:
  - [cleanup] Refactored Token, Tokenizer, ExpressionNode — commit `da1e1a28`
  - [imports] Created branch `scarpet-imports` from last working cleanup commit
  - [imports] Modified Tokenizer and Operators to support `import`, `from`, `as` as language operators with proper precedence.
  - [imports] Created global AST and State cache (`moduleASTCache`, `moduleStateCache`) in `ScriptServer.java`.
  - [imports] Transitioned `ScriptHost.java` to use lexical scoping and by-reference global structures, eliminating deep copies.
  - [imports] Added `importAliases` to `ModuleData` to natively resolve function and variable references dynamically.
  - [imports] Added `import_test.sc` to demonstrate the syntax.
- **Open Blockers**:
  - None

## Implementazione Import System Cache & Alias (COMPLETATO)

Il sistema di import è stato esteso per permettere di importare variabili, intere librerie, o singole funzioni usando alias (`as`). 
Il parser evita di ricalcolare l'AST (`ExpressionNode`) due volte sfruttando la `moduleASTCache` globale.
La `ModuleData` è ora unica per server e condivisa by-reference (Singleton/Lexical Scoping).
I test per le compilazioni (`./gradlew test`) sono un successo. Ho creato un test nativo in Scarpet (`import_test.sc`) pronto per l'esecuzione in-game per la verifica finale delle mutazioni by-reference.
