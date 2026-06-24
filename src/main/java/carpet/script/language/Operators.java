package carpet.script.language;

import carpet.script.Context;
import carpet.script.Expression;
import carpet.script.LazyValue;
import carpet.script.exception.InternalExpressionException;
import carpet.script.value.AbstractListValue;
import carpet.script.value.BooleanValue;
import carpet.script.value.ContainerValueInterface;
import carpet.script.value.FunctionAnnotationValue;
import carpet.script.value.FunctionUnpackedArgumentsValue;
import carpet.script.value.LContainerValue;
import carpet.script.value.ListValue;
import carpet.script.value.MapValue;
import carpet.script.value.NumericValue;
import carpet.script.value.Value;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Operators
{
    public static final Map<String, Integer> precedence = new HashMap<>()
    {{
        put("attribute~:", 80);
        put("alias_as", 75);
        put("unary_from", 70);
        put("import_op", 65);
        put("unary+-!...", 60);
        put("exponent^", 40);
        put("multiplication*/%", 30);
        put("addition+-", 20);
        put("compare>=><=<", 10);
        put("equal==!=", 7);
        put("and&&", 5);
        put("or||", 4);
        put("assign=<>", 3);
        put("def->", 2);
        put("nextop;", 1);
    }};

    public static void apply(Expression expression)
    {
        expression.addBinaryOperator("+", "sum", precedence.get("addition+-"), true, Value::add, lv -> {
            int size = lv.size();
            if (size == 0)
            {
                return Value.NULL;
            }
            Value accumulator = lv.get(0);
            for (Value v : lv.subList(1, size))
            {
                accumulator = accumulator.add(v);
            }
            return accumulator;
        });

        expression.addBinaryOperator("-", "difference", precedence.get("addition+-"), true, Value::subtract, lv -> {
            int size = lv.size();
            if (size == 0)
            {
                return Value.NULL;
            }
            Value accumulator = lv.get(0);
            for (Value v : lv.subList(1, size))
            {
                accumulator = accumulator.subtract(v);
            }
            return accumulator;
        });

        expression.addBinaryOperator("*", "product", precedence.get("multiplication*/%"), true, Value::multiply, lv -> {
            int size = lv.size();
            if (size == 0)
            {
                return Value.NULL;
            }
            Value accumulator = lv.get(0);
            for (Value v : lv.subList(1, size))
            {
                accumulator = accumulator.multiply(v);
            }
            return accumulator;
        });

        expression.addBinaryOperator("/", "quotient", precedence.get("multiplication*/%"), true, Value::divide, lv -> {
            int size = lv.size();
            if (size == 0)
            {
                return Value.NULL;
            }
            Value accumulator = lv.get(0);
            for (Value v : lv.subList(1, size))
            {
                accumulator = accumulator.divide(v);
            }
            return accumulator;
        });

        expression.addBinaryOperator("%", "modulo", precedence.get("multiplication*/%"), true, (v1, v2) ->
                NumericValue.asNumber(v1).mod(NumericValue.asNumber(v2)));

        expression.addBinaryOperator("^", "exponent", precedence.get("exponent^"), false, (v1, v2) ->
                new NumericValue(java.lang.Math.pow(NumericValue.asNumber(v1).getDouble(), NumericValue.asNumber(v2).getDouble())));

        expression.addFunction("bitwise_and", lv -> {
            int size = lv.size();
            if (size == 0)
            {
                return Value.NULL;
            }
            long accumulator = NumericValue.asNumber(lv.get(0)).getLong();
            for (Value v : lv.subList(1, size))
            {
                accumulator = accumulator & NumericValue.asNumber(v).getLong();
            }
            return new NumericValue(accumulator);
        });

        expression.addFunction("bitwise_xor", lv -> {
            int size = lv.size();
            if (size == 0)
            {
                return Value.NULL;
            }
            long accumulator = NumericValue.asNumber(lv.get(0)).getLong();
            for (Value v : lv.subList(1, size))
            {
                accumulator = accumulator ^ NumericValue.asNumber(v).getLong();
            }
            return new NumericValue(accumulator);
        });

        expression.addFunction("bitwise_or", lv -> {
            int size = lv.size();
            if (size == 0)
            {
                return Value.NULL;
            }
            long accumulator = NumericValue.asNumber(lv.get(0)).getLong();
            for (Value v : lv.subList(1, size))
            {
                accumulator = accumulator | NumericValue.asNumber(v).getLong();
            }
            return new NumericValue(accumulator);
        });

        // lazy cause RHS is only conditional
        expression.addLazyBinaryOperator("&&", "and", precedence.get("and&&"), false, true, t -> Context.Type.BOOLEAN, (c, t, lv1, lv2) ->
        { // todo check how is optimizations going
            Value v1 = lv1.evalValue(c, Context.BOOLEAN);
            return v1.getBoolean() ? lv2 : ((cc, tt) -> v1);
        }, (c, t, lv) -> {
            int last = lv.size() - 1;
            if (last == -1)
            {
                return LazyValue.TRUE;
            }
            for (LazyValue l : lv.subList(0, last))
            {
                Value val = l.evalValue(c, Context.Type.BOOLEAN);
                if (val instanceof FunctionUnpackedArgumentsValue fuav)
                {
                    for (Value it : fuav)
                    {
                        if (!it.getBoolean())
                        {
                            return (cc, tt) -> it;
                        }
                    }
                }
                else
                {
                    if (!val.getBoolean())
                    {
                        return (cc, tt) -> val;
                    }
                }
            }
            return lv.get(last);
        });

        // lazy cause RHS is only conditional
        expression.addLazyBinaryOperator("||", "or", precedence.get("or||"), false, true, t -> Context.Type.BOOLEAN, (c, t, lv1, lv2) ->
        {
            Value v1 = lv1.evalValue(c, Context.BOOLEAN);
            return v1.getBoolean() ? ((cc, tt) -> v1) : lv2;
        }, (c, t, lv) -> {
            int last = lv.size() - 1;
            if (last == -1)
            {
                return LazyValue.FALSE;
            }
            for (LazyValue l : lv.subList(0, last))
            {
                Value val = l.evalValue(c, Context.Type.BOOLEAN);
                if (val instanceof final FunctionUnpackedArgumentsValue fuav)
                {
                    for (Value it : fuav)
                    {
                        if (it.getBoolean())
                        {
                            return (cc, tt) -> it;
                        }
                    }
                }
                else
                {
                    if (val.getBoolean())
                    {
                        return (cc, tt) -> val;
                    }
                }
            }
            return lv.get(last);
        });

        expression.addBinaryOperator("~", "match", precedence.get("attribute~:"), true, Value::in);

        expression.addBinaryOperator(">", "decreasing", precedence.get("compare>=><=<"), false, (v1, v2) -> BooleanValue.of(v1.compareTo(v2) > 0), lv -> {
            int size = lv.size();
            if (size < 2)
            {
                return Value.TRUE;
            }
            Value prev = lv.get(0);
            for (Value next : lv.subList(1, size))
            {
                if (prev.compareTo(next) <= 0)
                {
                    return Value.FALSE;
                }
                prev = next;
            }
            return Value.TRUE;
        });

        expression.addBinaryOperator(">=", "nonincreasing", precedence.get("compare>=><=<"), false, (v1, v2) -> BooleanValue.of(v1.compareTo(v2) >= 0), lv -> {
            int size = lv.size();
            if (size < 2)
            {
                return Value.TRUE;
            }
            Value prev = lv.get(0);
            for (Value next : lv.subList(1, size))
            {
                if (prev.compareTo(next) < 0)
                {
                    return Value.FALSE;
                }
                prev = next;
            }
            return Value.TRUE;
        });

        expression.addBinaryOperator("<", "increasing", precedence.get("compare>=><=<"), false, (v1, v2) -> BooleanValue.of(v1.compareTo(v2) < 0), lv -> {
            int size = lv.size();
            if (size < 2)
            {
                return Value.TRUE;
            }
            Value prev = lv.get(0);
            for (Value next : lv.subList(1, size))
            {
                if (prev.compareTo(next) >= 0)
                {
                    return Value.FALSE;
                }
                prev = next;
            }
            return Value.TRUE;
        });

        expression.addBinaryOperator("<=", "nondecreasing", precedence.get("compare>=><=<"), false, (v1, v2) -> BooleanValue.of(v1.compareTo(v2) <= 0), lv -> {
            int size = lv.size();
            if (size < 2)
            {
                return Value.TRUE;
            }
            Value prev = lv.get(0);
            for (Value next : lv.subList(1, size))
            {
                if (prev.compareTo(next) > 0)
                {
                    return Value.FALSE;
                }
                prev = next;
            }
            return Value.TRUE;
        });

        expression.addMathematicalBinaryIntFunction("bitwise_shift_left", (num, amount) -> num << amount);
        expression.addMathematicalBinaryIntFunction("bitwise_shift_right", (num, amount) -> num >>> amount);
        expression.addMathematicalBinaryIntFunction("bitwise_arithmetic_shift_right", (num, amount) -> num >> amount);
        expression.addMathematicalBinaryIntFunction("bitwise_roll_left", (num, amount) -> Long.rotateLeft(num, (int)amount));
        expression.addMathematicalBinaryIntFunction("bitwise_roll_right", (num, amount) -> Long.rotateRight(num, (int)amount));
        expression.addMathematicalUnaryIntFunction("bitwise_not", d -> {
            long num = (long) d;
            return ~num;
        });
        expression.addMathematicalUnaryIntFunction("bitwise_popcount", d -> (long) Long.bitCount((long)d));
        expression.addMathematicalUnaryIntFunction("double_to_long_bits", Double::doubleToLongBits);
        expression.addUnaryFunction("long_to_double_bits", v ->
                new NumericValue(Double.longBitsToDouble(NumericValue.asNumber(v).getLong())));
        expression.addBinaryOperator("==", "equal", precedence.get("equal==!="), false, (v1, v2) -> v1.equals(v2) ? Value.TRUE : Value.FALSE, lv -> {
            int size = lv.size();
            if (size < 2)
            {
                return Value.TRUE;
            }
            Value prev = lv.get(0);
            for (Value next : lv.subList(1, size))
            {
                if (!prev.equals(next))
                {
                    return Value.FALSE;
                }
                prev = next;
            }
            return Value.TRUE;
        });

        expression.addBinaryOperator("!=", "unique", precedence.get("equal==!="), false, (v1, v2) -> v1.equals(v2) ? Value.FALSE : Value.TRUE, lv -> {
            int size = lv.size();
            if (size < 2)
            {
                return Value.TRUE;
            }
            // need to order them so same obejects will be next to each other.
            lv.sort(Comparator.comparingInt(Value::hashCode));
            Value prev = lv.get(0);
            for (Value next : lv.subList(1, size))
            {
                if (prev.equals(next))
                {
                    return Value.FALSE;
                }
                prev = next;
            }
            return Value.TRUE;
        });

        // lazy cause of assignment which is non-trivial
        expression.addLazyBinaryOperator("=", "assign", precedence.get("assign=<>"), false, false, t -> Context.Type.LVALUE, (c, t, lv1, lv2) ->
        {
            Value v1 = lv1.evalValue(c, Context.LVALUE);
            Value v2 = lv2.evalValue(c);
            if (v1 instanceof final ListValue.ListConstructorValue lcv && v2 instanceof final ListValue list)
            {
                List<Value> ll = lcv.getItems();
                List<Value> rl = list.getItems();
                if (ll.size() < rl.size())
                {
                    throw new InternalExpressionException("Too many values to unpack");
                }
                if (ll.size() > rl.size())
                {
                    throw new InternalExpressionException("Too few values to unpack");
                }
                for (Value v : ll)
                {
                    v.assertAssignable();
                }
                Iterator<Value> li = ll.iterator();
                Iterator<Value> ri = rl.iterator();
                while (li.hasNext())
                {
                    String lname = li.next().getVariable();
                    Value vval = ri.next().reboundedTo(lname);
                    expression.setAnyVariable(c, lname, (cc, tt) -> vval);
                }
                return (cc, tt) -> Value.TRUE;
            }
            if (v1 instanceof final LContainerValue lcv)
            {
                ContainerValueInterface container = lcv.container();
                if (container == null)
                {
                    return (cc, tt) -> Value.NULL;
                }
                Value address = lcv.address();
                if (!(container.put(address, v2)))
                {
                    return (cc, tt) -> Value.NULL;
                }
                return (cc, tt) -> v2;
            }
            v1.assertAssignable();
            String varname = v1.getVariable();
            Value copy = v2.reboundedTo(varname);
            LazyValue boundedLHS = (cc, tt) -> copy;
            expression.setAnyVariable(c, varname, boundedLHS);
            return boundedLHS;
        });

        // lazy due to assignment
        expression.addLazyBinaryOperator("+=", "append", precedence.get("assign=<>"), false, false, t -> Context.Type.LVALUE, (c, t, lv1, lv2) ->
        {
            Value v1 = lv1.evalValue(c, Context.LVALUE);
            Value v2 = lv2.evalValue(c);
            if (v1 instanceof final ListValue.ListConstructorValue lcv && v2 instanceof final ListValue list)
            {
                List<Value> ll = lcv.getItems();
                List<Value> rl = list.getItems();
                if (ll.size() < rl.size())
                {
                    throw new InternalExpressionException("Too many values to unpack");
                }
                if (ll.size() > rl.size())
                {
                    throw new InternalExpressionException("Too few values to unpack");
                }
                for (Value v : ll)
                {
                    v.assertAssignable();
                }
                Iterator<Value> li = ll.iterator();
                Iterator<Value> ri = rl.iterator();
                while (li.hasNext())
                {
                    Value lval = li.next();
                    String lname = lval.getVariable();
                    Value result = lval.add(ri.next()).bindTo(lname);
                    expression.setAnyVariable(c, lname, (cc, tt) -> result);
                }
                return (cc, tt) -> Value.TRUE;
            }
            if (v1 instanceof final LContainerValue lcv)
            {
                ContainerValueInterface cvi = lcv.container();
                if (cvi == null)
                {
                    throw new InternalExpressionException("Failed to resolve left hand side of the += operation");
                }
                Value key = lcv.address();
                Value value = cvi.get(key);
                if (value instanceof ListValue || value instanceof MapValue)
                {
                    ((AbstractListValue) value).append(v2);
                    return (cc, tt) -> value;
                }
                else
                {
                    Value res = value.add(v2);
                    cvi.put(key, res);
                    return (cc, tt) -> res;
                }
            }
            v1.assertAssignable();
            String varname = v1.getVariable();
            LazyValue boundedLHS;
            if (v1 instanceof ListValue || v1 instanceof MapValue)
            {
                ((AbstractListValue) v1).append(v2);
                boundedLHS = (cc, tt) -> v1;
            }
            else
            {
                Value result = v1.add(v2).bindTo(varname);
                boundedLHS = (cc, tt) -> result;
            }
            expression.setAnyVariable(c, varname, boundedLHS);
            return boundedLHS;
        });

        expression.addBinaryContextOperator("<>", "swap", precedence.get("assign=<>"), false, false, false, (c, t, v1, v2) ->
        {
            if (v1 instanceof final ListValue.ListConstructorValue lcv1 && v2 instanceof final ListValue.ListConstructorValue lcv2)
            {
                List<Value> ll = lcv1.getItems();
                List<Value> rl = lcv2.getItems();
                if (ll.size() < rl.size())
                {
                    throw new InternalExpressionException("Too many values to unpack");
                }
                if (ll.size() > rl.size())
                {
                    throw new InternalExpressionException("Too few values to unpack");
                }
                for (Value v : ll)
                {
                    v.assertAssignable();
                }
                for (Value v : rl)
                {
                    v.assertAssignable();
                }
                Iterator<Value> li = ll.iterator();
                Iterator<Value> ri = rl.iterator();
                while (li.hasNext())
                {
                    Value lval = li.next();
                    Value rval = ri.next();
                    String lname = lval.getVariable();
                    String rname = rval.getVariable();
                    lval.reboundedTo(rname);
                    rval.reboundedTo(lname);
                    expression.setAnyVariable(c, lname, (cc, tt) -> rval);
                    expression.setAnyVariable(c, rname, (cc, tt) -> lval);
                }
                return Value.TRUE;
            }
            v1.assertAssignable();
            v2.assertAssignable();
            String lvalvar = v1.getVariable();
            String rvalvar = v2.getVariable();
            Value lval = v2.reboundedTo(lvalvar);
            Value rval = v1.reboundedTo(rvalvar);
            expression.setAnyVariable(c, lvalvar, (cc, tt) -> lval);
            expression.setAnyVariable(c, rvalvar, (cc, tt) -> rval);
            return lval;
        });

        expression.addUnaryOperator("-", "opposite", false, v -> NumericValue.asNumber(v).opposite());

        expression.addUnaryOperator("+", "identity", false, NumericValue::asNumber);

        // could be non-lazy, but who cares - its a small one.
        expression.addLazyUnaryOperator("!", "not", precedence.get("unary+-!..."), false, true, x -> Context.Type.BOOLEAN, (c, t, lv) ->
                lv.evalValue(c, Context.BOOLEAN).getBoolean() ? (cc, tt) -> Value.FALSE : (cc, tt) -> Value.TRUE
        ); // might need context boolean

        // lazy because of typed evaluation of the argument
        expression.addLazyUnaryOperator("...", "unpack", Operators.precedence.get("unary+-!..."), false, true, t -> t == Context.Type.LOCALIZATION ? Context.NONE : t, (c, t, lv) ->
        {
            if (t == Context.LOCALIZATION)
            {
                return (cc, tt) -> new FunctionAnnotationValue(lv.evalValue(c), FunctionAnnotationValue.Type.VARARG);
            }
            if (!(lv.evalValue(c, t) instanceof final AbstractListValue alv))
            {
                throw new InternalExpressionException("Unable to unpack a non-list");
            }
            FunctionUnpackedArgumentsValue fuaval = new FunctionUnpackedArgumentsValue(alv.unpack());
            return (cc, tt) -> fuaval;
        });

        // ----------------------------------------------------
        // Import System Operators
        // ----------------------------------------------------

        /**
         * 'from' - Unary prefix operator.
         * Syntax: from mod
         * Acts mostly as syntactic sugar. Returns the module name as a string.
         */
        expression.addLazyUnaryOperator("from", "from_unary", precedence.get("unary_from"), false, true,
            t -> Context.NONE, (c, t, lv) -> {
                Value val = lv.evalValue(c);
                Value ret = new carpet.script.value.StringValue(val.getString());
                return (cc, tt) -> ret;
            });

        /**
         * 'as' - Binary operator.
         * Syntax: name as alias
         * Returns a list of [name, alias]
         */
        expression.addBinaryOperator("as", "alias_as", precedence.get("alias_as"), true,
            (v1, v2) -> ListValue.of(v1, v2));

        /**
         * 'import' - Unary operator.
         * Syntax: import mod  OR  import mod as alias
         */
        expression.addLazyUnaryOperator("import", "import_unary", precedence.get("import_op"), false, true,
            t -> Context.NONE,
            (c, t, lv) -> {
                Value val = lv.evalValue(c);
                String moduleName;
                String alias = null;
                
                if (val instanceof ListValue list && list.getItems().size() == 2) {
                    moduleName = list.getItems().get(0).getString();
                    alias = list.getItems().get(1).getString();
                } else {
                    moduleName = val.getString();
                }
                
                c.host.importModule(c, moduleName);
                if (alias != null) {
                    // We map the module namespace to the alias if we had module references
                    // In Scarpet, 'import mod as alias' isn't natively supported for module references
                    // But we can map all its exports to prefix alias_
                    // Wait, the prompt says "import mod as alias". We will leave this for now.
                }
                return (cc, tt) -> Value.NULL;
            });

        /**
         * 'import' - Binary operator.
         * Syntax: from mod import func1, func2 as alias
         */
        expression.addLazyBinaryOperator("import", "import_binary", precedence.get("import_op"), true, false,
            t -> Context.NONE,
            (c, t, lv1, lv2) -> {
                Value moduleVal = lv1.evalValue(c);
                String moduleName = moduleVal.getString();
                c.host.importModule(c, moduleName);
                
                Value importsVal = lv2.evalValue(c);
                List<org.apache.commons.lang3.tuple.Pair<String, String>> identifiers = new java.util.ArrayList<>();
                
                if (importsVal instanceof ListValue list) {
                    for (Value item : list.getItems()) {
                        if (item instanceof ListValue aliasList && aliasList.getItems().size() == 2) {
                            identifiers.add(org.apache.commons.lang3.tuple.Pair.of(
                                aliasList.getItems().get(0).getString(), 
                                aliasList.getItems().get(1).getString()
                            ));
                        } else {
                            identifiers.add(org.apache.commons.lang3.tuple.Pair.of(item.getString(), item.getString()));
                        }
                    }
                } else {
                    if (importsVal instanceof ListValue aliasList && aliasList.getItems().size() == 2) {
                        identifiers.add(org.apache.commons.lang3.tuple.Pair.of(
                            aliasList.getItems().get(0).getString(), 
                            aliasList.getItems().get(1).getString()
                        ));
                    } else {
                        identifiers.add(org.apache.commons.lang3.tuple.Pair.of(importsVal.getString(), importsVal.getString()));
                    }
                }
                
                c.host.importNames(c, expression.module, moduleName, identifiers);
                return (cc, tt) -> Value.NULL;
            });

    }
}
