/*
 * The MIT License
 *
 * Copyright 2012 Universidad de Montemorelos A. C.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package mx.edu.um.portlets.esu.dao;

import mx.edu.um.portlets.esu.model.Dia;
import org.joda.time.DateTime;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:context/applicationContext.xml", "classpath:context/portlet/inicio.xml"})
@Transactional
public class DiaDaoTest {
    
    private static final Logger log = LoggerFactory.getLogger(DiaDaoTest.class);
    @Autowired
    private DiaDao instance;
    
    public DiaDaoTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of obtiene method, of class DiaDao.
     */
    @Test
    public void testObtiene() {
        log.debug("obtiene");
        DateTime hoy = new DateTime();
        hoy.plusDays(1);
        Dia dia = instance.obtiene(hoy);
        assertNull(dia);
    }

    /**
     * Test of guarda method, of class DiaDao.
     */
    @Test
    public void testGuarda() {
        log.debug("guarda");
        Dia dia = new Dia(new DateTime().toDate(), 1l, 1l, 1l, 1l, 1l, 1l, 1l, 1l, 1l);
        dia = instance.guarda(dia);
        assertNotNull(dia);
    }

}
