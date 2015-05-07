/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Gilberto Torrezan Filho
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 */
package com.github.gilbertotorrezan.gwtviews.client;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Gilberto Torrezan Filho
 *
 * @since v.1.0.0
 */
@RunWith(JUnit4.class)
public class URLTokenTest {

	@Test
	public void testSimpleURL(){
		URLToken token = new URLToken("testing");
		
		Assert.assertEquals("testing", token.getId());
		Assert.assertEquals("testing", token.toString());
		Assert.assertEquals(null, token.getParameter("param1"));
	}
	
	@Test
	public void testURLWithParameters(){
		URLToken token = new URLToken("testing/with/parameters&param1&param2=yay&param3");
		
		Assert.assertEquals("testing/with/parameters", token.getId());
		Assert.assertEquals("testing/with/parameters&param1&param2=yay&param3", token.toString());
		Assert.assertEquals("", token.getParameter("param1"));
		Assert.assertEquals("yay", token.getParameter("param2"));
		Assert.assertEquals("", token.getParameter("param3"));
		Assert.assertEquals(null, token.getParameter("param4"));
	}
	
	@Test
	public void testURLWithComplexParameters(){
		URLToken token = new URLToken("testing/with/complex/parameters&param2=yay&param3='this&is=complex'&param1&param4=''");
		
		Assert.assertEquals("testing/with/complex/parameters", token.getId());
		Assert.assertEquals("testing/with/complex/parameters&param2=yay&param3='this&is=complex'&param1&param4", token.toString());
		Assert.assertEquals("", token.getParameter("param1"));
		Assert.assertEquals("yay", token.getParameter("param2"));
		Assert.assertEquals("this&is=complex", token.getParameter("param3"));
		Assert.assertEquals("", token.getParameter("param4"));
		Assert.assertEquals(null, token.getParameter("param5"));
	}
	
	@Test
	public void testEmpty(){
		URLToken token = new URLToken("");
		
		Assert.assertEquals("", token.getId());
		Assert.assertEquals("", token.toString());
		Assert.assertEquals(null, token.getParameter("param1"));
	}
	
	@Test
	public void testNull(){
		URLToken token = new URLToken(null);
		
		Assert.assertEquals("", token.getId());
		Assert.assertEquals("", token.toString());
		Assert.assertEquals(null, token.getParameter("param1"));
	}
	
}
