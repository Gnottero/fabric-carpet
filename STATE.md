# STATE

- **Current Branch**: scarpet-components (from `f2540102`)
- **Last Commit**: `3d522cd0` Implement native Data Component support for ItemStacks
- **Completed Tasks**:
  - [cleanup] Refactored Token, Tokenizer, ExpressionNode — commit `da1e1a28`
  - [imports] Created branch `scarpet-imports` from last working cleanup commit
  - [imports] Modified Tokenizer and Operators to support `import`, `from`, `as` as language operators with proper precedence.
  - [imports] Created global AST and State cache (`moduleASTCache`, `moduleStateCache`) in `ScriptServer.java`.
  - [imports] Transitioned `ScriptHost.java` to use lexical scoping and by-reference global structures, eliminating deep copies.
  - [imports] Added `importAliases` to `ModuleData` to natively resolve function and variable references dynamically.
  - [imports] Added `import_test.sc` to demonstrate the syntax.
  - [components] Created branch `scarpet-components` from last working imports commit
  - [components] Implemented native Data Component support in Scarpet replacing legacy NBT limitations via Minecraft 1.20.5+ API
  - [components] Added `item_component`, `item_default_component`, `modify_item_component`, `remove_item_component` functions to `Inventories.java`
  - [components] Provided `components_test.sc` script with unit tests demonstrating the use of alias components manipulation
- **Open Blockers**:
  - None

## Implementazione Data Components (COMPLETATO)

Il sistema di analisi NBT-based limitato di Scarpet è stato affiancato ed espanso dal supporto nativo per i Data Component tipizzati (inclusi codec per le conversioni dinamiche Tag <-> Value).
Sono state aggiunte 4 nuove funzioni native builtin a `Inventories.java`:
1. `item_component`: Decodifica e restituisce un component in formato `Value`.
2. `item_default_component`: Interroga `Item#components()` per recuperare il valore hidden/predefinito della tipologia base.
3. `modify_item_component`: Serializza dinamicamente un parametro generico Value riapplicando le modifiche come `DataComponent`.
4. `remove_item_component`: Rimuove il data component tipizzato dall'istanza ItemStack.

Le modifiche sono atomiche e localizzate in `Inventories.java`, riducendo i token usati e massimizzando l'efficacia per le versioni recenti del gioco, evitando conflitti logici. Un test completo è pronto in `components_test.sc`.
