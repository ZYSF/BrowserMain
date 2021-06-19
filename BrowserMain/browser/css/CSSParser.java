package browser.css;

public abstract class CSSParser {
	public static enum Combinators {
		ADJACENTSIBLING,
		GENERALSIBLING,
		CHILD,
		DESCENDANT
	}
	public static enum BasicSelectors {
		TYPE,
		CLASS,
		ID,
		UNIVERSAL,
		PSEUDOCLASS,
		PSEUDOELEMENT,
		ATTRIBUTESET,
		ATTRIBUTEEXACT,
		ATTRIBUTELIST,
		ATTRIBUTEHYPHEN,
		ATTRIBUTECONTAIN,
		ATTRIBUTEBEGIN,
		ATTRIBUTEEND
	}
	public static enum FunctionType {
		NONE,
		ATTR,
		CALC,
		CUBICBEZIER,
		HSL,
		HSLA,
		LINEARGRADIENT,
		RADIALGRADIENT,
		REPLINEARGRADIENT,
		REPRADIALGRADIENT,
		RGB,
		RGBA,
		URL,
		VAR,
		UNKNOWN
	}
	
	public static enum State {
		STANDBY,
		ADJACENT,
		GENERAL,
		CHILD,
		PSEUDO,
		ID,
		CLASS,
		UNIVERSAL,
		SELECTORS,
		PROPERTIES,
		PROPERTIES_END,
		VALUES,
		VALUES_QUOT,
		VALUES_END,
		VALUES_FUNC,
		ATRULE,
		ATTRIBUTE,
		ATTRIBUTE_TYPE,
		ATTRIBUTE_VALUE_BEGIN,
		ATTRIBUTE_VALUE_END,
		ATTRIBUTE_END
	}

	public CSSParser() {
		// TODO Auto-generated constructor stub
	}

	public abstract void addComment(String comment);
	public abstract void addSelector(BasicSelectors type, String name);
	public abstract void addSelectorCombinator(Combinators combinator);
	public abstract void addSelectorComma();
	public abstract void beginProperties();
	public abstract void endProperties();
	public abstract void beginProperty(String property);
	public abstract void addValue(String value);
	public abstract void addFuncValue(String func, String args);
	public abstract void addAttributeSelector(BasicSelectors type, String attribute, String value);
	
}
