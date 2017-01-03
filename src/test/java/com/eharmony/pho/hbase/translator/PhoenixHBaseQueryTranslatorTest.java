package com.eharmony.pho.hbase.translator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.eharmony.pho.hbase.model.TranslationTestClass;
import com.eharmony.pho.mapper.EntityPropertiesMappingContext;
import com.eharmony.pho.mapper.EntityPropertiesResolver;
import com.eharmony.pho.query.QuerySelect;
import com.eharmony.pho.query.QueryUpdate;
import com.eharmony.pho.query.builder.QueryBuilder;
import com.eharmony.pho.query.builder.QueryUpdateBuilder;
import com.eharmony.pho.query.criterion.Ordering;
import com.eharmony.pho.query.criterion.Restrictions;

public class PhoenixHBaseQueryTranslatorTest {

    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss z";
    private final DateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
    private EntityPropertiesResolver entityPropertiesResolver = null;

    @Before
    public void setUp() throws ClassNotFoundException {
        final List<String> classesList = new ArrayList<String>();
        classesList.add("com.eharmony.pho.hbase.model.TranslationTestClass");
        classesList.add("com.eharmony.pho.hbase.model.EmbededEntityExample");
        EntityPropertiesMappingContext context = new EntityPropertiesMappingContext(classesList);
        entityPropertiesResolver = new EntityPropertiesResolver(context);
    }
    
    @Test
    public void testStringLike() throws ParseException, ClassNotFoundException {
    	
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        String city = "Angeles";
        String result = translator.like("fname", city);
        String expected = "fname LIKE '%Angeles%'";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void testStringILike() throws ParseException, ClassNotFoundException {
    	
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        String city = "Angeles";
        String result = translator.insensitiveLike("fname", city);
        String expected = "fname ILIKE '%Angeles%'";

        Assert.assertEquals(expected, result);
    }
    
    @Test
    public void testDateEq() throws ParseException, ClassNotFoundException {

        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        String dateString = "2011-12-19 18:35:34 PDT PST";
        Date date = getDate(dateString);
        String result = translator.eq("deliveryDate", date);
        Assert.assertNotNull(result);
        System.out.println(result);
        Assert.assertTrue(StringUtils.containsAny(result, dateString));
        //String expected = "deliveryDate = TO_DATE('2011-12-19 17:35:34 PST', 'yyyy-MM-dd HH:mm:ss z')";
        //Assert.assertEquals(expected, result);
    }

    @Test
    public void testStringEq() throws ParseException, ClassNotFoundException {

        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        String name = "vijay";
        String result = translator.eq("fname", name);
        Assert.assertNotNull(result);
        System.out.println(result);
        Assert.assertTrue(StringUtils.containsAny(result, name));
        Assert.assertEquals("fname = 'vijay'", result);
    }

    @Test
    public void testIntegerEq() throws ParseException, ClassNotFoundException {
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        int uid = 3000;
        String result = translator.eq("uid", uid);
        Assert.assertNotNull(result);
        System.out.println(result);
        Assert.assertEquals("uid = 3000", result);
    }

    @Test
    public void testBooleanEq() throws ParseException, ClassNotFoundException {
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        boolean isUser = true;
        String result = translator.eq("isu", isUser);
        Assert.assertNotNull(result);
        System.out.println(result);
        Assert.assertEquals("isu = true", result);
    }

    @Test
    public void testLongEq() throws ParseException, ClassNotFoundException {
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        long mid = 12l;
        String result = translator.eq("mid", mid);
        Assert.assertNotNull(result);
        System.out.println(result);
        Assert.assertEquals("mid = 12", result);
    }

    @Test
    public void testFloatEq() throws ParseException, ClassNotFoundException {
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        float distance = 12.5f;
        String result = translator.eq("distance", distance);
        Assert.assertNotNull(result);
        System.out.println(result);
        Assert.assertEquals("distance = 12.5", result);
    }

    private Date getDate(String date) throws ParseException {
        return dateFormat.parse(date);
    }

    @Test
    public void testTranslateSelect() throws ClassNotFoundException {
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        QuerySelect<TranslationTestClass, TranslationTestClass> query = QueryBuilder
                .builderFor(TranslationTestClass.class).select().add(Restrictions.eq("uid", 2)).build();
        String queryStr = translator.translate(query);
        System.out.println(queryStr);
        Assert.assertTrue(StringUtils.contains(queryStr, "SELECT"));

    }

    @Test
    public void testTranslateSelectWithProjection() throws ClassNotFoundException {
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        List<String> fields = new LinkedList<String>();
        fields.add("name");
        fields.add("userId");
        fields.add("createdAt");
        fields.add("pwd");
        QuerySelect<TranslationTestClass, TranslationTestClass> query = QueryBuilder
                .builderFor(TranslationTestClass.class).select(fields).add(Restrictions.eq("userId", 2)).build();
        String queryStr = translator.translate(query);
        System.out.println(queryStr);
        Assert.assertTrue(StringUtils.contains(queryStr, "SELECT"));

    }
    
    @Test
    public void testTranslateSelectWithCriteria() throws ClassNotFoundException {
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        QuerySelect<TranslationTestClass, TranslationTestClass> query = QueryBuilder
                .builderFor(TranslationTestClass.class).select()
                .add(Restrictions.eq("userId", 2))
                .add(Restrictions.eq("name", "vija'y"))
                .add(Restrictions.gte("createdAt", new Date()))
                .build();
        String queryStr = translator.translate(query);
        System.out.println(queryStr);
        Assert.assertTrue(StringUtils.contains(queryStr, "SELECT"));
        Assert.assertFalse(StringUtils.contains(queryStr, "ORDER"));

    }
    
    @Test
    public void testTranslateSelectWithCriteriaAndOrder() throws ClassNotFoundException {
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        QuerySelect<TranslationTestClass, TranslationTestClass> query = QueryBuilder
                .builderFor(TranslationTestClass.class).select()
                .add(Restrictions.eq("userId", 2))
                .add(Restrictions.eq("name", "vija'y"))
                .addOrder(Ordering.desc("createdAt"))
                .build();
        String queryStr = translator.translate(query);
        System.out.println(queryStr);
        Assert.assertTrue(StringUtils.contains(queryStr, "SELECT"));
        Assert.assertTrue(StringUtils.contains(queryStr, "DESC"));

    }
    
    @Test
    public void testTranslateSelectWithCriteriaAndOrderAndValidLimit() throws ClassNotFoundException {
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        QuerySelect<TranslationTestClass, TranslationTestClass> query = QueryBuilder
                .builderFor(TranslationTestClass.class).select()
                .add(Restrictions.eq("userId", 2))
                .add(Restrictions.eq("name", "vija'y"))
                .addOrder(Ordering.desc("createdAt"))
                .setMaxResults(10)
                .build();
        String queryStr = translator.translate(query);
        Assert.assertTrue(StringUtils.contains(queryStr, "SELECT"));
        Assert.assertTrue(StringUtils.contains(queryStr, "DESC"));
        Assert.assertTrue(StringUtils.contains(queryStr, "ORDER BY created_date DESC NULLS FIRST LIMIT 10"));
    }
    
    @Test
    public void testTranslateSelectWithCriteriaAndOrderAndInValidLimit() throws ClassNotFoundException {
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        QuerySelect<TranslationTestClass, TranslationTestClass> query = QueryBuilder
                .builderFor(TranslationTestClass.class).select()
                .add(Restrictions.eq("userId", 2))
                .add(Restrictions.eq("name", "vija'y"))
                .addOrder(Ordering.desc("createdAt"))
                .setMaxResults(0)
                .build();
        String queryStr = translator.translate(query);
        System.out.println(queryStr);
        Assert.assertTrue(StringUtils.contains(queryStr, "SELECT"));
        Assert.assertTrue(StringUtils.contains(queryStr, "DESC"));
        Assert.assertFalse(StringUtils.contains(queryStr, "LIMIT 10"));

    }
    
    @Test
    public void testTranslateUpsertWithQuoteInName() throws ClassNotFoundException {
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        QueryUpdate<TranslationTestClass> query = QueryUpdateBuilder
                .builderFor(buildTestClassObjectA())
                .update()
                .build();
        String queryStr = translator.translate(query);
        System.out.println(queryStr);
        Assert.assertTrue(StringUtils.contains(queryStr, "UPSERT"));
        Assert.assertTrue(StringUtils.contains(queryStr, "Plain''fiekd''"));

    }
    
    @Test
    public void testTranslateUpsertWithEmptySelectedFields() throws ClassNotFoundException {
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        QueryUpdate<TranslationTestClass> query = QueryUpdateBuilder
                .builderFor(buildTestClassObjectB())
                .update(new ArrayList<String>())
                .build();
        
        String queryStr = translator.translate(query);
        System.out.println(queryStr);
        Assert.assertTrue(StringUtils.contains(queryStr, "UPSERT"));
        Assert.assertTrue(StringUtils.contains(queryStr, "special~!@#$%^&"));

    }
    
    @Test
    public void testTranslateUpsertWithNullFields() throws ClassNotFoundException {
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        QueryUpdate<TranslationTestClass> query = QueryUpdateBuilder
                .builderFor(buildTestClassObjectC())
                .update(new ArrayList<String>())
                .build();
        
        String queryStr = translator.translate(query);
        System.out.println(queryStr);
        Assert.assertTrue(StringUtils.contains(queryStr, "UPSERT"));

    }
    
    @Test
    public void testIsNull() throws ParseException, ClassNotFoundException {
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        String result = translator.isNull("myField");
        Assert.assertNotNull(result);
        Assert.assertEquals("myField IS NULL", result);
    }
    
    @Test
    public void testIsNotNull() throws ParseException, ClassNotFoundException {
        PhoenixHBaseQueryTranslator translator = new PhoenixHBaseQueryTranslator(entityPropertiesResolver);
        String result = translator.notNull("myField");
        Assert.assertNotNull(result);
        Assert.assertEquals("myField IS NOT NULL", result);
    }
    
    private TranslationTestClass buildTestClassObjectA() {
        TranslationTestClass testClass = new TranslationTestClass();
        testClass.setName("Plain'fiekd'\\\\\\");
        testClass.setUserId(1);
        return testClass;
    }
    
    private TranslationTestClass buildTestClassObjectB() {
        TranslationTestClass testClass = new TranslationTestClass();
        testClass.setName("special~!@#$%^&*()_+=-:;\"'?/>.<,|\\\\}]{[clas's\\");
        testClass.setUserId(1);
        return testClass;
    }
    
    private TranslationTestClass buildTestClassObjectC() {
        TranslationTestClass testClass = new TranslationTestClass();
        testClass.setName(null);
        testClass.setUserId(1);
        return testClass;
    }


}
