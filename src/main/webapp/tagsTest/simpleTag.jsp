<%@ page contentType="text/html; charset=GBK"%>
<%@ page import="java.util.*"%>
<%
        //����һ��List����
        List<String> a = new ArrayList<String>();
        a.add("hello");
        a.add("world");
        a.add("java");
        //��List�������page��Χ��
        pageContext.setAttribute("a" , a);
%>
<!-- �����ǩ�⣬ָ��mytagǰ׺�ı�ǩ��	��http://www.lwkjob.org/mytag�ı�ǩ�⴦�� -->
<%@ taglib uri="http://www.lwkjob.org/simpleTag" prefix="simpleTag"%>
<%@ taglib uri="http://www.lwkjob.org/queryTag" prefix="queryTag"%>
<%@ taglib uri="http://www.lwkjob.org/queryTag" prefix="queryTag"%>
<%@ taglib uri="http://www.lwkjob.org/iterator" prefix="iterator"%>
<html>
<head>
<title>�Զ����ǩʾ��</title>
</head>
<body bgcolor="#ffffc0">
	<h2>������ʾ�����Զ����ǩ�е�����</h2>
	<!-- ʹ�ñ�ǩ ������mytag�Ǳ�ǩǰ׺������taglib�ı���ָ�
	mytagǰ׺����http://www.lwkjob.org/mytag�ı�ǩ�⴦�� -->
	<!-- �򵥱�ǩ -->
	<simpleTag:helloWorld />
	<!-- �����Եı�ǩ -->
	<queryTag:query user="root" url="jdbc:mysql://localhost:3306/cms" pass="111111" driver="com.mysql.jdbc.Driver" sql="select * from t_article"/>
	 
        <table border="1"  width="300">
        <!-- ʹ�õ�������ǩ����a���Ͻ��е��� -->
        <iterator:iterator collection="a" item="item">
            <tr>
                <td>${pageScope.item}</td>
            <tr>
        </iterator:iterator>
        </table>
</body>
</html>