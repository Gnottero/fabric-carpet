package carpet.script;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import carpet.script.value.Value;

// WIP
public abstract class ScriptServer
{
    public final Map<Value, Value> systemGlobals = new ConcurrentHashMap<>();

    // Global cache for the import system
    public final Map<String, carpet.script.Expression.ExpressionNode> moduleASTCache = new ConcurrentHashMap<>();
    public final Map<String, carpet.script.ScriptHost.ModuleData> moduleStateCache = new ConcurrentHashMap<>();

    public abstract Path resolveResource(String suffix);
}
