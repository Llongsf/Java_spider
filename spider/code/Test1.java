import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;


public class Test1 
{
    //  定义论文总篇数记录
    public static int total_numberOf_papers = 0;

    public String get_Keywords() throws UnsupportedEncodingException 
    {
        System.out.print("Please input your keywords you want to search:");
        Scanner scan = new Scanner(System.in,"GBK");

        String scan_key = scan.nextLine();

        scan.close();
        
        return scan_key;
    }

    public static String join_Strings(List<String> alist)
    {
        String finals = new String();
        // 创建一个字符串合并类对象
        StringJoiner joiner = new StringJoiner("; ");
        for (String element : alist) 
        {
            joiner.add(element);
        }
        // 将joiner内容变为字符串形式
        finals = joiner.toString();
        return finals;
    }
    public void stop(int ii)
    {
        try 
        {
            Thread.sleep(ii);
        } 
        catch (Exception e) 
        {
            
            e.printStackTrace();
        }
    }

    

    public static void main(String[] args) 
    {
        // 菜单界面
        System.out.println("     | Welcome to Paper-spider system! |\n");
        System.out.print(
                "  1. China Zhiwang\n" + 
                "  2. Douban\n" +
                "  3. Wanfang\n" +
                "Please choose the website you want to collect papers:");
        // 创建对象，保存输入的选项
        Scanner Scanner = new Scanner(System.in);
        int type = Scanner.nextInt();

        System.out.print("\nPlease input searching pages: ");
        // 要爬取的页数
        int number_Of_pages = Scanner.nextInt();

        // 定义爬虫测试类对象
        Zhiwang_spider zhiwang = new Zhiwang_spider();
        Douban_spider douban = new Douban_spider();
        Wanfang_spider wanfang = new Wanfang_spider();
        switch(type) 
        {
            case 1:
            // 爬取知网论文
            zhiwang.collect_Zhiwang(number_Of_pages);
            break;

            case 2:
            douban.collect_Douban(number_Of_pages);
            break;

            case 3:
            wanfang.collect_Wanfang(number_Of_pages);

            default:
            System.out.println("Please input a right number!");
        }
        Scanner.close();
    }
    
    public void saveToDatabase( int total_num, String title, String author,String time,String abstracts, String link, int types) 
    {
        // 配置数据库连接
        String url = "jdbc:mysql://localhost:3306/jdbc?useSSL=false&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "root";

        // 建立数据库连接
        try (Connection conn = DriverManager.getConnection(url, username, password)) 
        {
            String search_sql = "SELECT id FROM paper ORDER BY id DESC LIMIT 1"; // 查询数据库最后一行的 id

            // 创建 Statement 对象
            PreparedStatement statement_pre = conn.prepareStatement(search_sql);

            // 执行查询操作
            ResultSet resultSet = statement_pre.executeQuery();

            int nextId = 0;

            if(resultSet.next())
            {
                // 让数据库下一次开始的存储ID能够紧接末行id
                nextId = resultSet.getInt("id") + 1;
            } 

            // 关闭PreparedStatement
            statement_pre.close();
            resultSet.close();

            String sql = new String();// 准备 SQL 插入语句
            sql = "INSERT INTO `paper` (`id`, `title`, `author`, `Date`, `abstract`, `Link`, `Type`) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            String type1 = new String();
            if (types == 2)
            {
                type1 = "Movie comment";
            }
            else if (types == 1)
            {
                type1 = "paper information";
            }

            
            // 创建 PreparedStatement 对象
            PreparedStatement statement = conn.prepareStatement(sql);

            // 设置参数
            statement.setInt(1, nextId);
            statement.setString(2, title);
            statement.setString(3, author);
            statement.setString(4, time);
            statement.setString(5, abstracts);
            statement.setString(6, link);
            statement.setString(7, type1);

            // 执行插入操作
            statement.executeUpdate();

            // 关闭 PreparedStatement
            statement.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}