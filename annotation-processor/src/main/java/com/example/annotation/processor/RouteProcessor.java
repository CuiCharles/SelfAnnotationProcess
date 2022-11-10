package com.example.annotation.processor;

import com.example.annotation.Route;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;


@AutoService(Processor.class)
public class RouteProcessor extends AbstractProcessor {


	private String PKG = "com.charles.test.annotation";

	private HashMap<String ,TypeElement> routes = new HashMap<>();

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public synchronized void init(ProcessingEnvironment env){
		super.init(env);
	}

	/**
	 * 用于申明，该注解处理器需要处理的注解，只有包含这些注解的才会被该注解处理器处理。
	 * @return
	 */
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Collections.singleton(Route.class.getCanonicalName());
	}


	/**
	 * 用于实现处理注解的主要逻辑，包括解析被@Route标注过的类，根据@Route的参数url和对应类生成一张HashMap，然后根据表生成Java代码。
	 * @param set
	 * @param roundEnvironment
	 * @return
	 */
	@Override
	public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

		if (roundEnvironment.processingOver()) {
			generateSource();
		} else {
			parseAnnotation(roundEnvironment);
		}

		return true;
	}


	/**
	 *解析{@link com.example.annotation.Route}注解，把被它标注过的类加入HashMap中
	 * @param roundEnvironment
	 */
	private void parseAnnotation(RoundEnvironment roundEnvironment) {

		for(Element element : roundEnvironment.getElementsAnnotatedWith(Route.class)){
			if (element.getKind() != ElementKind.CLASS) {
				return;
			}

			TypeElement typeElement = (TypeElement) element;
			Route uri = element.getAnnotation(Route.class);
			routes.put(uri.url(), typeElement);
		}

	}

	/**
	 *
	 * 根据HashMap生成Java代码
	 *
	 */
	private void generateSource() {
		TypeSpec.Builder navigatorClass = TypeSpec.classBuilder("Routers")
				.addModifiers(Modifier.PUBLIC, Modifier.FINAL);

		MethodSpec.Builder getMethod = MethodSpec.methodBuilder("get")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.addParameter(ClassName.get(String.class), "url")
				.returns(ClassName.get(Class.class));
		List<Map.Entry<String, TypeElement>> entrySet = new ArrayList<>(routes.entrySet());
		for (int i = 0; i < entrySet.size(); i++) {
			Map.Entry<String, TypeElement> entry = entrySet.get(i);
			ClassName activityClass = ClassName.get(entry.getValue());
			if (i == 0) {
				getMethod.beginControlFlow("if (url.equals($S))", entry.getKey());
			} else {
				getMethod.nextControlFlow("else if (url.equals($S))", entry.getKey());
			}
			getMethod.addStatement("return $T.class", activityClass);
			if (i == entrySet.size() - 1) {
				getMethod.endControlFlow();
			}
		}
		getMethod.addStatement("return null");
		navigatorClass.addMethod(getMethod.build());
		try {
			Filer filer = processingEnv.getFiler();
			TypeSpec typeSpec = navigatorClass.build();
			JavaFile.builder(PKG, typeSpec).build().writeTo(filer);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("dddddddddddddddddddddddddddddd" +e.toString());
		}

	}
}
