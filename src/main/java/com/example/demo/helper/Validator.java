package com.example.demo.helper;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.fasterxml.jackson.databind.JsonNode;
//import net.sf.json.JSONObject;

public class Validator {
	
	//private static Log log = LogFactory.getLog(JsonShemaValidator.class);
	//private final static JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
	
	
	//public Validator() {
	//}
	
    public void validation(net.sf.json.JSONObject json) throws ValidationException{
			JSONObject jsonSchema = new JSONObject(
				      new JSONTokener(JsonSchema.class.getResourceAsStream("schema_02.json")));
			Schema schema = SchemaLoader.load(jsonSchema);
		    schema.validate(json);  
	}
    
    public boolean validation(String jsonString) throws Exception{
    	
    	//final JsonNode jsonSchema = JsonLoader.fromResource("C:\\Users\\ASUS\\Desktop\\schema.json");
    	final JsonNode jsonSchema = JsonLoader.fromResource("/schema.json");
    	final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        
        final JsonSchema schema = factory.getJsonSchema(jsonSchema);
		JsonNode jsonNode = JsonLoader.fromString(jsonString);
		ProcessingReport report=schema.validate(jsonNode);
		
		return report.isSuccess();
    }
	
}
