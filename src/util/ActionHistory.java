package util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

/**
 * Generic Stack-based Undo/Redo manager.
 *
 * Data Structure: Two stacks (ArrayDeque used as Stack — LIFO).
 *   undoStack: holds actions that can be undone.
 *   redoStack: holds undone actions that can be re-applied.
 *
 * Usage pattern with lambdas:
 *   ActionHistory<Order> history = new ActionHistory<>();
 *   // Add an item
 *   history.execute(
 *       order,
 *       o -> o.addItem(beverage),       // do
 *       o -> o.removeItem(beverage)     // undo
 *   );
 *   history.undo();  // removes the item
 *   history.redo();  // adds it back
 *
 * T10 – UndoManager for VinTony POS Loyalty System
 */
public class ActionHistory<T> {

    // ── Reversible action record ──────────────────────────────────────────────
    public static class Action<T> {
        private final String      description;
        private final T           target;
        private final Consumer<T> doAction;
        private final Consumer<T> undoAction;

        public Action(String description, T target,
                      Consumer<T> doAction, Consumer<T> undoAction) {
            this.description = description;
            this.target      = target;
            this.doAction    = doAction;
            this.undoAction  = undoAction;
        }

        public void execute() { doAction.accept(target); }
        public void undo()    { undoAction.accept(target); }
        public String getDescription() { return description; }
    }

    // ── Stacks ────────────────────────────────────────────────────────────────
    private final Deque<Action<T>> undoStack = new ArrayDeque<>();
    private final Deque<Action<T>> redoStack = new ArrayDeque<>();

    // Maximum history kept (prevents memory bloat in long sessions)
    private final int maxSize;

    public ActionHistory() { this(50); }

    public ActionHistory(int maxSize) {
        if (maxSize <= 0) throw new IllegalArgumentException("maxSize must be > 0");
        this.maxSize = maxSize;
    }

    // ── Core API ──────────────────────────────────────────────────────────────

    /**
     * Execute a new action and push it onto the undo stack.
     * Clears the redo stack (any undone actions are lost once a new action happens).
     */
    public void execute(String description, T target,
                        Consumer<T> doAction, Consumer<T> undoAction) {
        Action<T> action = new Action<>(description, target, doAction, undoAction);
        action.execute();                 // perform the action
        undoStack.push(action);           // record it
        redoStack.clear();                // branching: redo history invalidated

        // cap history size
        if (undoStack.size() > maxSize) {
            undoStack.removeLast();
        }
    }

    /**
     * Undo the most recent action.
     * @return description of undone action, or null if nothing to undo.
     */
    public String undo() {
        if (undoStack.isEmpty()) return null;
        Action<T> action = undoStack.pop();
        action.undo();
        redoStack.push(action);
        return "Undone: " + action.getDescription();
    }

    /**
     * Redo the most recently undone action.
     * @return description of redone action, or null if nothing to redo.
     */
    public String redo() {
        if (redoStack.isEmpty()) return null;
        Action<T> action = redoStack.pop();
        action.execute();
        undoStack.push(action);
        return "Redone: " + action.getDescription();
    }

    // ── State queries ─────────────────────────────────────────────────────────
    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }

    public String peekUndo() {
        return undoStack.isEmpty() ? null : undoStack.peek().getDescription();
    }
    public String peekRedo() {
        return redoStack.isEmpty() ? null : redoStack.peek().getDescription();
    }

    public int undoSize() { return undoStack.size(); }
    public int redoSize() { return redoStack.size(); }

    /** Clear all history (e.g. when starting a new session). */
    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }
}
