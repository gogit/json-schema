/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.json.schema.loader;

import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.ResourceLoader;
import org.everit.json.schema.Schema;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;

public class DefinesPropertyTest {

    private static JSONObject ALL_SCHEMAS = ResourceLoader.DEFAULT.readObj("testschemas.json");

    private JSONObject get(final String schemaName) {
        return ALL_SCHEMAS.getJSONObject(schemaName);
    }

    @Test
    public void objectSchemaHasField() {
        ObjectSchema actual = (ObjectSchema) SchemaLoader.load(get("pointerResolution"));
        Assert.assertTrue(actual.definesProperty("#/rectangle"));
        Assert.assertTrue(actual.definesProperty("#/rectangle/a"));
        Assert.assertTrue(actual.definesProperty("#/rectangle/b"));

        Assert.assertFalse(actual.definesProperty("#/rectangle/c"));
        Assert.assertFalse(actual.definesProperty("#/rectangle/"));
        Assert.assertFalse(actual.definesProperty("#/"));
        Assert.assertFalse(actual.definesProperty("#/a"));
        Assert.assertFalse(actual.definesProperty("#"));
        Assert.assertFalse(actual.definesProperty("#/rectangle/a/d"));
    }

    @Test
    public void recursiveSchemaHasField() {
        Schema recursiveSchema = SchemaLoader.load(get("recursiveSchema"));

        Assert.assertTrue(recursiveSchema.definesProperty("#/prop"));
        Assert.assertTrue(recursiveSchema.definesProperty("#/prop/subprop"));
        Assert.assertTrue(recursiveSchema.definesProperty("#/prop/subprop/subprop"));
        Assert.assertTrue(recursiveSchema.definesProperty("#/prop/subprop/subprop/subprop"));
    }

    @Test
    public void patternPropertiesHasField() {
        ObjectSchema actual = (ObjectSchema) SchemaLoader.load(get("patternProperties"));
        Assert.assertTrue(actual.definesProperty("#/a"));
        Assert.assertTrue(actual.definesProperty("#/aa"));
        Assert.assertTrue(actual.definesProperty("#/aaa"));
        Assert.assertTrue(actual.definesProperty("#/aaaa"));
        Assert.assertTrue(actual.definesProperty("#/aaaaa"));

        Assert.assertFalse(actual.definesProperty("b"));
    }

    @Test
    public void objectWithSchemaDep() {
        ObjectSchema actual = (ObjectSchema) SchemaLoader.load(get("objectWithSchemaDep"));
        Assert.assertTrue(actual.definesProperty("#/a"));
        Assert.assertTrue(actual.definesProperty("#/b"));

        Assert.assertFalse(actual.definesProperty("#/c"));
    }

    @Test
    public void objectWithSchemaRectangleDep() {
        ObjectSchema actual = (ObjectSchema) SchemaLoader.load(get("objectWithSchemaRectangleDep"));
        Assert.assertTrue(actual.definesProperty("#/d"));
        Assert.assertTrue(actual.definesProperty("#/rectangle/a"));
        Assert.assertTrue(actual.definesProperty("#/rectangle/b"));

        Assert.assertFalse(actual.definesProperty("#/c"));
        Assert.assertFalse(actual.definesProperty("#/d/c"));
        Assert.assertFalse(actual.definesProperty("#/rectangle/c"));
    }

    @Test
    public void objectEscape() {
        ObjectSchema actual = (ObjectSchema) SchemaLoader.load(get("objectEscape"));
        Assert.assertTrue(actual.definesProperty("#/a~0b"));
        Assert.assertTrue(actual.definesProperty("#/a~0b/c~1d"));

        Assert.assertFalse(actual.definesProperty("#/a~0b/c/d"));
    }

    @Test
    public void testOfTest() {
        ObjectSchema actual = (ObjectSchema) SchemaLoader.load(get("patternPropsAndSchemaDeps"));
        JSONObject input = ResourceLoader.DEFAULT
                .readObj("objecttestcases.json")
                .getJSONObject("validOfPatternPropsAndSchemaDeps");
        actual.validate(input);
    }

    @Test
    public void patternPropsAndSchemaDefs() {
        ObjectSchema actual = (ObjectSchema) SchemaLoader.load(get("patternPropsAndSchemaDeps"));
        // Assert.assertTrue(actual.definesProperty("#/1stLevel"));
        // Assert.assertTrue(actual.definesProperty("#/1stLevel/2ndLevel"));
        Assert.assertTrue(actual.definesProperty("#/1stLevel/2ndLevel/3rdLev"));
        // Assert.assertTrue(actual.definesProperty("#/1stLevel/2ndLevel/3rdLevel/4thLevel"));
    }

}
