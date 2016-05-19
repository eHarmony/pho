package com.eharmony.pho.hbase.mapper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSetMetaData;
import java.util.Iterator;

import org.apache.phoenix.jdbc.PhoenixResultSet;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
public class PhoenixProjectedResultMapperTest {

	
	@Test
	public void testMapResultsResultSetClassOfR_Long() throws Exception {
		
		PhoenixProjectedResultMapper mapper = new PhoenixProjectedResultMapper(null);
		@SuppressWarnings("resource")
        PhoenixResultSet rs = mock(PhoenixResultSet.class);
		ResultSetMetaData rsMeta = mock(ResultSetMetaData.class);
		
		when(rs.getMetaData()).thenReturn(rsMeta);
		when(rs.next()).thenAnswer(new Answer<Boolean>() {
			boolean t = false;

			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				t = !t;
				return t;
			}
			
		});
		when(rsMeta.getColumnCount()).thenReturn(1);
		when(rsMeta.getColumnName(1)).thenReturn("count");
		when(rs.getObject(any())).thenReturn(new Long(1000));
		Iterable<Long> longL = mapper.mapResults(rs, Long.class);
		assertNotNull(longL);
		Iterator<Long> it = longL.iterator();
		assertTrue(it.next() == 1000);
		assertFalse(it.hasNext());
	}
	
	@Test
	public void testMapResultsResultSetClassOfR_Int() throws Exception {
		
		PhoenixProjectedResultMapper mapper = new PhoenixProjectedResultMapper(null);
		@SuppressWarnings("resource")
        PhoenixResultSet rs = mock(PhoenixResultSet.class);
		ResultSetMetaData rsMeta = mock(ResultSetMetaData.class);
		
		when(rs.getMetaData()).thenReturn(rsMeta);
		when(rs.next()).thenAnswer(new Answer<Boolean>() {
			boolean t = false;

			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				t = !t;
				return t;
			}
			
		});
		when(rsMeta.getColumnCount()).thenReturn(1);
		when(rsMeta.getColumnName(1)).thenReturn("count");
		when(rs.getObject(any())).thenReturn(new Integer(1000));
		Iterable<Integer> intL = mapper.mapResults(rs, Integer.class);
		assertNotNull(intL);
		Iterator<Integer> it = intL.iterator();
		assertTrue(it.next() == 1000);
		assertFalse(it.hasNext());
	}

}
