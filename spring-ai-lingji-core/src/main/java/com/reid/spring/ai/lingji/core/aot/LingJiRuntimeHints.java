package com.reid.spring.ai.lingji.core.aot;

import com.reid.spring.ai.lingji.core.api.chat.LingJiChatApi;
import com.reid.spring.ai.lingji.core.api.files.LingJiFilesApi;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import static org.springframework.ai.aot.AiRuntimeHints.findJsonAnnotatedClassesInPackage;

public class LingJiRuntimeHints implements RuntimeHintsRegistrar {

	@Override
	public void registerHints(@NonNull RuntimeHints hints, @Nullable ClassLoader classLoader) {
		var mcs = MemberCategory.values();
		for (var tr : findJsonAnnotatedClassesInPackage(LingJiChatApi.class))
			hints.reflection().registerType(tr, mcs);
		for (var tr : findJsonAnnotatedClassesInPackage(LingJiFilesApi.class))
			hints.reflection().registerType(tr, mcs);
	}

}
