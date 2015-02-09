package de.bitdroid.jaxrs2retrofit;

import com.thoughtworks.qdox.builder.impl.EvaluatingVisitor;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.expression.FieldRef;


final class SimpleEvaluatingVisitor extends EvaluatingVisitor {

	private final JavaClass context;

	public SimpleEvaluatingVisitor(JavaClass context) {
		this.context = context;
	}

	@Override
	public Object getFieldReferenceValue(JavaField field) {
		String expression = field.getInitializationExpression();
		if (expression.startsWith("\"")) expression = expression.substring(1);
		if (expression.endsWith("\"")) expression = expression.substring(0, expression.length() - 1);
		return expression;
	}


	@Override
	public Object visit(FieldRef fieldRef) {
		try {
			return super.visit(fieldRef);
		} catch (IllegalArgumentException iae) {
			// let's try again! (some refs are not found unfortunately ...)
			JavaPackage currentPackage = context.getPackage();
			JavaClass targetClass = null;
			for (JavaClass c : currentPackage.getClasses()) {
				if (c.getName().equals(fieldRef.getNamePart(0))) {
					targetClass = c;
				}
			}

			if (targetClass == null) throw iae;

			JavaField field = targetClass.getFieldByName(fieldRef.getNamePart(1));
			return getFieldReferenceValue(field);
		}
	}

}
