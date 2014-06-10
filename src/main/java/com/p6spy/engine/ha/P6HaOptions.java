/*
 * #%L
 * P6Spy
 * %%
 * Copyright (C) 2002 - 2014 P6Spy
 * %%
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
 * #L%
 */
package com.p6spy.engine.ha;

import com.p6spy.engine.spy.option.P6OptionsRepository;

import javax.management.StandardMBean;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * User: kataev
 * Date: 10.06.14
 */
public class P6HaOptions extends StandardMBean implements P6HaLoadableOptions {

    public static final String SQLEXPRESSION = "sqlexpression";
    public static final String SQLEXPRESSION_PATTERN = "sqlexpressionPattern";

    private final P6OptionsRepository optionsRepository;

    public static final Map<String, String> defaults;

    static {
        defaults = new HashMap<String, String>();
    }

    public P6HaOptions(final P6OptionsRepository optionsRepository) {
        super(P6HaOptionsMBean.class, false);
        this.optionsRepository = optionsRepository;
    }

    @Override
    public void load(Map<String, String> options) {
        setSQLExpression(options.get(SQLEXPRESSION));
    }

    @Override
    public Map<String, String> getDefaults() {
        return defaults;
    }

    @Override
    public String getSQLExpression() {
        return optionsRepository.get(String.class, SQLEXPRESSION);
    }

    @Override
    public Pattern getSQLExpressionPattern() {
        return optionsRepository.get(Pattern.class, SQLEXPRESSION_PATTERN);
    }

    @Override
    public void setSQLExpression(String sqlexpression) {
        optionsRepository.set(String.class, SQLEXPRESSION, sqlexpression);
        optionsRepository.set(Pattern.class, SQLEXPRESSION_PATTERN, sqlexpression);
    }

    @Override
    public void unSetSQLExpression() {
        optionsRepository.setOrUnSet(String.class, SQLEXPRESSION, null, defaults.get(SQLEXPRESSION));
        optionsRepository.setOrUnSet(Pattern.class, SQLEXPRESSION_PATTERN, null, defaults.get(SQLEXPRESSION_PATTERN));
    }
}
