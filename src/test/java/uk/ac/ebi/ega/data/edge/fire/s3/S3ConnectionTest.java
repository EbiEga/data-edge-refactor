/*
 *
 * Copyright 2020 EMBL - European Bioinformatics Institute
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
 *
 */
package uk.ac.ebi.ega.data.edge.fire.s3;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URISyntaxException;

@RunWith(SpringRunner.class)
@Ignore("Requires connection to Fire, Generated files be uploaded on Fire test environment. Gitlab is not setup for " +
        "this yet")
@TestPropertySource("classpath:application-test.properties")
//@SpringBootTest(classes = {S3ConnectionTest.class, FileValidationApplication.class})
public class S3ConnectionTest {

    private static Logger logger = LoggerFactory.getLogger(S3ConnectionTest.class);

    @Autowired
    //private IValidationService validationService;

    @Test
    public void test() throws URISyntaxException {
        // public void test() throws URISyntaxException, AlgorithmInitializationException {

  /*      // File that can't be decrypted
        //fire://ukbb/hh-decode-01/2020-03-09T1754_1265799/1265799.g.vcf.gz.1583842845000.cip
        //doProcess("ukbb/hh-decode-01/2020-03-09T1754_1265799/1265799.g.vcf.gz.1583842845000.cip", 8117527538L);
        final ValidationResult result = validationService.validate(
                new Validation(
                        new URI("fire://ukbb/hh-decode-01/2020-04-15T1716_1940388/1940388.g.vcf.gz.tbi.1587003291689" +
                                ".cip"),
                        "",
                        "d5936be1e2fc8e86f1a88963f34efa90", 4210910,
                        "42be2f564299c807433da799eb8446a2", 4210894
                ));
        logger.info("Result: {}", result);

        final ValidationResult result2 = validationService.validate(
                new Validation(
                        new URI("fire://ukbb/hl-decode-jvc-01/gatk_chr13_074900001-074950000_2020-09-04T0934/gatk_chr13_074900001-074950000.vcf.gz.tbi.1599431009554.cip"),
                        "",
                        "348365926a71942d9e6749d124289491", 304,
                        "b0142a6e7ba8e53ab7974644f9f40562", 288
                ));
        logger.info("Result: {}", result2);
    }*/

    }
}