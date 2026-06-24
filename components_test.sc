// components_test.sc
// Dimostra la manipolazione dei Data Components tramite le nuove funzioni built-in

// Creiamo un item di test
item = ['diamond_sword', 1, null];

print('Test 1: Aggiunta custom component');
// Aggiungiamo un danno alla spada
item = modify_item_component(item, 'minecraft:damage', 10);
// Verifichiamo che il component sia stato aggiunto
val = item_component(item, 'minecraft:damage');
if (val == 10, print('PASS - Damage = 10'), print('FAIL - Damage = ' + val));

print('Test 2: Modifica component esistente');
item = modify_item_component(item, 'minecraft:damage', 50);
val2 = item_component(item, 'minecraft:damage');
if (val2 == 50, print('PASS - Damage = 50'), print('FAIL - Damage = ' + val2));

print('Test 3: Rimozione component');
item = remove_item_component(item, 'minecraft:damage');
val3 = item_component(item, 'minecraft:damage');
if (val3 == null, print('PASS - Component removed'), print('FAIL - Component non rimosso: ' + val3));

print('Test 4: Estrazione component di default (ispezione nascosti)');
def_val = item_default_component(item, 'minecraft:max_stack_size');
if (def_val == 1, print('PASS - Default max stack size = 1'), print('FAIL - Stack size = ' + def_val));
def_val2 = item_default_component(['stone', 1, null], 'minecraft:max_stack_size');
if (def_val2 == 64, print('PASS - Stone Default max stack size = 64'), print('FAIL - Stack size = ' + def_val2));

print('Tutti i test completati.');
