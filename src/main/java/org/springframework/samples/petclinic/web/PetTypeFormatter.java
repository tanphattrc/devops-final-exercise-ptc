/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.web;


import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.ClinicService;

/**
 * Instructs Spring MVC on how to parse and print elements of type 'PetType'. Starting from Spring 3.0, Formatters have
 * come as an improvement in comparison to legacy PropertyEditors. See the following links for more details: - The
 * Spring ref doc: http://static.springsource.org/spring/docs/current/spring-framework-reference/html/validation.html#format-Formatter-SPI
 * - A nice blog entry from Gordon Dickens: http://gordondickens.com/wordpress/2010/09/30/using-spring-3-0-custom-type-converter/
 * <p/>
 * Also see how the bean 'conversionService' has been declared inside /WEB-INF/mvc-core-config.xml
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Michael Isvy
 */
public class PetTypeFormatter implements Formatter<PetType> {

    private final ClinicService clinicService;


    @Autowired
    public PetTypeFormatter(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @Override
    public String print(PetType petType, Locale locale) {
        return petType.getName();
    }

    @Override
    public PetType parse(String text, Locale locale) throws ParseException {
        Collection<PetType> findPetTypes = this.clinicService.findPetTypes();
        for (PetType type : findPetTypes) {
            if (type.getName().equals(text)) {
                return type;
            }
        }
        throw new ParseException("type not found: " + text, 0);
    }

    public PetType parseV2(String text, Locale locale) throws ParseException {
        Collection<PetType> findPetTypes = this.clinicService.findPetTypes();
        for (PetType type : findPetTypes) {
            if (type.getName().equals(text)) {
                return type;
            }
        }
        throw new ParseException(Optional.ofNullable(getErrorMessage("notFound", locale)).map(String::toUpperCase).orElse("") + text, 0);
    }

    public static String getErrorMessage(String key, Locale locale, Object... params) {
        return getMessage(locale, "messages", key, params);
    }

    private static String getMessage(Locale locale, String baseName, String key, final Object... params) {
        String message = null;
        final ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
        try {
            message = bundle.getString(key);
        } catch (final MissingResourceException e) {
        }
        if (params != null) {
            final MessageFormat mf = new MessageFormat(message, locale);
            message = mf.format(params, new StringBuffer(), null).toString();
        }
        return message;
    }
}
