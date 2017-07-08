/*
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.dkpro.core.io.html;

import static de.tudarmstadt.ukp.dkpro.core.testing.IOTestRunner.testOneWay;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.util.CasUtil.select;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.jcas.JCas;
import org.junit.Rule;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.testing.DkproTestContext;
import de.tudarmstadt.ukp.dkpro.core.testing.dumper.CasDumpWriter;

public class HtmlReaderTest
{
    @Test
    public void wwwReaderTest()
        throws Exception
    {
        File targetDir = testContext.getTestOutputFolder();
        
        CollectionReaderDescription reader = createReaderDescription(
                HtmlReader.class,
                HtmlReader.PARAM_SOURCE_LOCATION, new URL("http://www.google.de")
        );
        
        AnalysisEngineDescription dumpWriter = createEngineDescription(CasDumpWriter.class, 
                CasDumpWriter.PARAM_TARGET_LOCATION, new File(targetDir, "google.html.dump"));

        for (JCas jcas : new JCasIterable(reader, dumpWriter)) {
            dumpMetaData(DocumentMetaData.get(jcas));
            assertEquals(1, select(jcas.getCas(), jcas.getDocumentAnnotationFs().getType()).size());

            assertTrue(jcas.getDocumentText().startsWith("Google"));
        }
    }
    
    @Test
    public void testReadFile()
        throws Exception
    {
        testOneWay(
                createReaderDescription(HtmlReader.class,
                        HtmlReader.PARAM_LANGUAGE, "en"), 
                "html/test.html.dump", 
                "html/test.html");
    }
    
    private void dumpMetaData(final DocumentMetaData aMetaData)
    {
        System.out.println("Collection ID: "+aMetaData.getCollectionId());
        System.out.println("ID           : "+aMetaData.getDocumentId());
        System.out.println("Base URI     : "+aMetaData.getDocumentBaseUri());
        System.out.println("URI          : "+aMetaData.getDocumentUri());
    }

    @Rule
    public DkproTestContext testContext = new DkproTestContext();
}