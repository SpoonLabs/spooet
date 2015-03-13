package spoon.reflect.visitor.internal;

import spoon.reflect.declaration.ModifierKind;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Created by nicolas on 24/02/2015.
 */
public final class ModifiersUtils {
	private ModifiersUtils() {
	}

	public static Modifier[] getReflect(Set<ModifierKind> modifierKind) {
		Collection<Modifier> result = new ArrayList<>();

		for (ModifierKind kind : modifierKind) {
			result.add(getReflect(kind));
		}

		return result.toArray(new Modifier[result.size()]);
	}

	public static ModifierKind getModifierKind(Modifier modifier) {
		switch (modifier) {
		case PUBLIC:
			return ModifierKind.PUBLIC;
		case PROTECTED:
			return ModifierKind.PROTECTED;
		case PRIVATE:
			return ModifierKind.PRIVATE;
		case ABSTRACT:
			return ModifierKind.ABSTRACT;
		case STATIC:
			return ModifierKind.STATIC;
		case FINAL:
			return ModifierKind.FINAL;
		case TRANSIENT:
			return ModifierKind.TRANSIENT;
		case VOLATILE:
			return ModifierKind.VOLATILE;
		case SYNCHRONIZED:
			return ModifierKind.SYNCHRONIZED;
		case NATIVE:
			return ModifierKind.NATIVE;
		case STRICTFP:
			return ModifierKind.STRICTFP;
		default:
			throw new IllegalArgumentException("unknown modifier");
		}
	}

	public static Modifier getReflect(ModifierKind modifierKind) {
		switch (modifierKind) {
		case PUBLIC:
			return Modifier.PUBLIC;
		case PROTECTED:
			return Modifier.PROTECTED;
		case PRIVATE:
			return Modifier.PRIVATE;
		case ABSTRACT:
			return Modifier.ABSTRACT;
		case STATIC:
			return Modifier.STATIC;
		case FINAL:
			return Modifier.FINAL;
		case TRANSIENT:
			return Modifier.TRANSIENT;
		case VOLATILE:
			return Modifier.VOLATILE;
		case SYNCHRONIZED:
			return Modifier.SYNCHRONIZED;
		case NATIVE:
			return Modifier.NATIVE;
		case STRICTFP:
			return Modifier.STRICTFP;
		default:
			throw new IllegalArgumentException("unable to get java modifier for " + modifierKind);
		}
	}

}
