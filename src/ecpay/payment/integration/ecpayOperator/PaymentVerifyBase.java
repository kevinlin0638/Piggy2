package ecpay.payment.integration.ecpayOperator;

import ecpay.payment.integration.errorMsg.ErrorMessage;
import ecpay.payment.integration.exception.EcpayException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentVerifyBase{
	protected String confPath = "C:/EcpayPayment.xml";
	protected Document doc;
	public PaymentVerifyBase(){
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String configPath = null;
		try {
            configPath = URLDecoder.decode("C:/EcpayPayment.xml", "UTF-8");
        }catch (UnsupportedEncodingException e){

        }
		//URL fileURL = this.getClass().getResource(configPath);
		doc = EcpayFunction.xmlParser(configPath);
		doc.getDocumentElement().normalize();
	}
	
	protected void requireCheck(String FieldName, String objValue, String require){
		if(require.equals("1") && objValue.isEmpty())
			throw new EcpayException(FieldName+"為必填");
	}
	
	protected void valueCheck(String type, String objValue, Element ele){
		if(objValue.isEmpty()){
			return;
		}
		if(type.equals("String")){
			if(ele.getElementsByTagName("pattern") != null){
				Pattern r = Pattern.compile(ele.getElementsByTagName("pattern").item(0).getTextContent().toString());
				Matcher m = r.matcher(objValue);
				if(!m.find())
					throw new EcpayException(ele.getAttribute("name")+ErrorMessage.COLUMN_RULE_ERROR);
			}
		} else if(type.equals("Opt")){
			List<String> opt = new ArrayList<String>();
			NodeList n = ele.getElementsByTagName("option");
			for(int i=0; i < n.getLength(); i++){
				opt.add(n.item(i).getTextContent().toString());
			}
			if(!opt.contains(objValue))
				throw new EcpayException(ele.getAttribute("name")+ErrorMessage.COLUMN_RULE_ERROR);
		} else if(type.equals("Int")){
			String mode = ele.getElementsByTagName("mode").item(0).getTextContent();
			String minimum = ele.getElementsByTagName("minimal").item(0).getTextContent();
			String maximum = ele.getElementsByTagName("maximum").item(0).getTextContent();
			if(objValue.isEmpty())
				throw new EcpayException(ele.getAttribute("name")+ErrorMessage.CANNOT_BE_EMPTY);
			int value = Integer.valueOf(objValue);
			if(mode.equals("GE") && value < Integer.valueOf(minimum))
				throw new EcpayException(ele.getAttribute("name")+"不能小於"+minimum);
			else if(mode.equals("LE") && value > Integer.valueOf(maximum))
				throw new EcpayException(ele.getAttribute("name")+"不能大於"+maximum);
			else if(mode.equals("BETWEEN") && value < Integer.valueOf(minimum) && value > Integer.valueOf(maximum))
				throw new EcpayException(ele.getAttribute("name")+"必須介於"+minimum+"和"+maximum+"之間");
			else if(mode.equals("EXCLUDE") && value >= Integer.valueOf(minimum) && value <= Integer.valueOf(maximum))
				throw new EcpayException(ele.getAttribute("name")+"必須小於"+minimum+"或大於"+maximum);
		} else if(type.equals("DepOpt")){
			// TODO
		} 
	}
}
