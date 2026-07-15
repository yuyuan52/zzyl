import com.zzyl.generator.util.VelocityInitializer;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.FileWriter;
import java.io.IOException;

public class VelocityDemoTest {

    public static void main(String[] args) throws IOException {
        //初始化模板引擎
        VelocityInitializer.initVelocity();

        //准备上下文数据
        VelocityContext context = new VelocityContext();
        context.put("message","加油昌平422");

        //获取一个模板
        Template template = Velocity.getTemplate("vms/index.html.vm");

        //输出在本地文件中
        FileWriter fileWriter = new FileWriter("E:\\code\\cp422\\index.html");

        //合并模板和上下文数据
        template.merge(context,fileWriter);

        //关闭流
        fileWriter.close();
    }
}
