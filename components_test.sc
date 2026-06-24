// components_test.sc
// Dimostra la manipolazione dei Data Components tramite le nuove funzioni built-in

// Creiamo un item di test
item = ['diamond_sword', 1, null];

print('Test 1: Aggiunta custom component');
// Aggiungiamo un danno alla spada
item = modify(item, 'component', 'minecraft:damage', 10);
// Verifichiamo che il component sia stato aggiunto
val = query(item, 'component', 'minecraft:damage');
if (val == 10, print('PASS - Damage = 10'), print('FAIL - Damage = ' + val));

print('Test 2: Modifica component esistente');
item = modify(item, 'component', 'minecraft:damage', 50);
val2 = query(item, 'component', 'minecraft:damage');
if (val2 == 50, print('PASS - Damage = 50'), print('FAIL - Damage = ' + val2));

print('Test 3: Rimozione component');
item = modify(item, 'component', 'minecraft:damage', null);
val3 = query(item, 'component', 'minecraft:damage');
if (val3 == null, print('PASS - Component removed'), print('FAIL - Component non rimosso: ' + val3));

print('Test 4: Estrazione component di default (ispezione nascosti)');
def_val = query(item, 'component', 'minecraft:max_stack_size', true);
if (def_val == 1, print('PASS - Default max stack size = 1'), print('FAIL - Stack size = ' + def_val));
def_val2 = query(['stone', 1, null], 'component', 'minecraft:max_stack_size', true);
if (def_val2 == 64, print('PASS - Stone Default max stack size = 64'), print('FAIL - Stack size = ' + def_val2));

print('Test 5: Recupero tutti i component');
all_comps = query(item, 'components');
if (length(all_comps) > 0, print('PASS - Trovati ' + length(all_comps) + ' components'), print('FAIL - Nessun component trovato'));

print('Tutti i test completati.');
