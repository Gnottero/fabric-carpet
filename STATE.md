# STATE

- **Current Branch**: scarpet-classes
- **Last Commit**: `379cc4c2` feat(scarpet-classes): Add ClassValue, InstanceValue and class built-ins
- **Completed Tasks**:
  - [cleanup] Refactored Token, Tokenizer, ExpressionNode — commit `da1e1a28`
  - [classes] Creato branch `scarpet-classes` da ultimo commit funzionante
  - [classes] ClassValue.java — definizione di classe (nome + costruttore + metodi) ✅
  - [classes] InstanceValue.java — istanza con field map + ContainerValueInterface ✅
  - [classes] Functions.java — built-in `class()`, `new()`, `call_method()`, `get_field()`, `set_field()`, `instance_of()` ✅
  - [classes] Operators.java — operatore `::` con precedenza massima ✅
  - [classes] build.gradle — JUnit 5 + platform-launcher ✅
  - [classes] ClassSystemTest.java — 5 test JUnit (no MC runtime) ✅
  - [classes] Commit `379cc4c2` — BUILD SUCCESSFUL, tutti i test passano ✅
- **Open Blockers**:
  - None

## Piano Implementativo — Sistema a Classi

### Approccio
Classi come zucchero sintattico tradotto in costrutti Scarpet esistenti:
- **`ClassValue`** — nuovo tipo Value che rappresenta la definizione di una classe (nome + costruttore + metodi come mappa nome→FunctionValue)
- **`InstanceValue`** — nuovo tipo Value che rappresenta un'istanza (class + mappa campi)
- **`::` operatore** — invocazione di metodo su un'istanza: `obj::method(args)` → inietta `self` come primo argomento

### File creati/modificati
1. ✅ `value/ClassValue.java` — definizione di classe
2. ✅ `value/InstanceValue.java` — istanza di classe
3. ✅ `language/Functions.java` — built-in `class(name, constructor, methods...)` e `new(class, args...)`
4. ✅ `language/Operators.java` — operatore `::` per method dispatch
5. ✅ `build.gradle` — JUnit 5 per test
6. ✅ `ClassSystemTest.java` — test unitari

### Prossimi passi possibili
- Sintassi `class Foo:` parser-level (zucchero sintattico vero)
- Ereditarietà
- Metodi speciali (`__str__`, `__eq__`, etc.)
- Integrazione con il sistema di eventi Carpet
