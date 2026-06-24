// import_test.sc
// Questo script dimostra e verifica il funzionamento del nuovo sistema di import e lexical scoping in Scarpet.

// --- Modulo: libreria ---
// Simuliamo un modulo che viene importato
// (In ambiente reale questo sarebbe salvato in libreria.sc)
global_count = 0;

def increment() -> (
    global_count += 1;
);

def get_count() -> (
    return(global_count);
);

// --- Script Principale ---
// Test 1: Importazione base con alias e mutazione per riferimento
import 'libreria' as lib;

print('Test 1: Import base e mutazione per riferimento');
lib:increment();
lib:increment();
val = lib:get_count();
if (val == 2, print('PASS - Counter is 2'), print('FAIL - Counter is ' + val));

// Test 2: Importazione di funzioni e variabili specifiche
from 'libreria' import increment as inc, global_count as c;

print('Test 2: Modifica via alias');
inc();
if (c == 3, print('PASS - Counter is 3 (via alias)'), print('FAIL - Counter is ' + c));

print('Test 3: Condivisione dello scope globale');
// Dato che le importazioni modificano per riferimento lo stato del modulo (singleton globale),
// 'lib:get_count()' deve ora restituire 3
val_finale = lib:get_count();
if (val_finale == 3, print('PASS - Lo scope è globale e condiviso'), print('FAIL - Mismatch di scope'));
