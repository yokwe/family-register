package yokwe.family.register;

import java.io.File;
import java.util.Arrays;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import yokwe.family.register.antlr.FamilyRegisterLexer;
import yokwe.util.FileUtil;

public class DumpToken {
	private static final org.slf4j.Logger logger = yokwe.util.LoggerUtil.getLogger();

	public static void main(String[] args) {
		logger.info("START");
		
		process();
		
		logger.info("STOP");
	}

	private static void process() {
		File[] files = new File("data/family-register").listFiles(o -> o.getName().endsWith(".txt"));
		Arrays.sort(files);
		
		for(var file: files) {
			logger.info("file  {}", file.getPath());
			
			final FamilyRegisterLexer lexer;
			{
				var string = FileUtil.read().file(file);
				var charStream = CharStreams.fromString(string);
				lexer = new FamilyRegisterLexer(charStream);
			}
			
			{
				lexer.reset();
				CommonTokenStream tokens = new CommonTokenStream(lexer);
				tokens.fill();
				logger.info("==== dump token ====");
				for (var token : tokens.getTokens()) {
					logger.info("token  {}  {}  {}  {}", token.getLine(), token.getCharPositionInLine(), token.getType(), token.getText());
				}
				logger.info("==== ==== ===== ====");
			}
		}
	}
	
}
