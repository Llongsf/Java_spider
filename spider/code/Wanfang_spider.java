import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;

public class Wanfang_spider extends Test1
{
    Test1 test1 = new Test1();
    public void collect_Wanfang(int numOfPages)
    {
        String keywords = new String();
        try
        {   
            // 获取搜索关键字  
            keywords = get_Keywords();
        }
        catch(Exception e)
        {
            System.out.println("coding error!");
            System.exit(0);
        }
                  
        // 设置 WebDriver 的路径
        System.setProperty("webdriver.edge.driver", "H:/edgedriver_win64/msedgedriver.exe");

        // 请求头
        // options.addArguments("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        
        // 创建 WebDriver 实例
        WebDriver driver = new EdgeDriver();

        // 导航到论文网站
        driver.get("https://w.wanfangdata.com.cn/index.html?index=true");

        // 设置超时时间不超过6秒
        driver.manage().timeouts().pageLoadTimeout(6, TimeUnit.SECONDS);
        
        // 获取输入框，输入selenium
        driver.findElement(By.id("search-input")).sendKeys(keywords);

        // 获取按钮，进行搜索
        driver.findElement(By.className("search-btn-group")).click();

        for(int page = 1; page <= numOfPages; page++) 
        {
            // 进程停顿一下
            try 
            {
                Thread.sleep(4000);
            }
            catch (Exception e) 
            {
                
                e.printStackTrace();
            }
            

            WebElement total_search = driver.findElement(By.className("list-wraper"));
            // 获取页面内所有论文链接标签
            List<WebElement> search_aElement_Results = total_search.findElements(By.className("normal-list"));

            // 题目列表
            List<String> titleList = new ArrayList<String>();
            // 链接列表
            List<String> herfList = new ArrayList<String>();
            // 时间列表
            List<String> timeList =new ArrayList<String>();
            // 作者列表
            List<String> authorList = new ArrayList<String>();
            // 摘要列表
            List<String> abstractList = new ArrayList<String>();

            try 
            {
                for(WebElement aElement: search_aElement_Results)
                {

                    // 获取题目
                    String title = aElement.findElement(By.className("ajust")).getText();
                    titleList.add(title);

                    // 获取作者
                    List<WebElement> authors_and_time = aElement.findElements(By.className("authors"));
                    // 获取下来的是element类型，需要转换为字符串,通过拼接多个字符串存储多个作者

                    String author = new String();
                    // 时间和作者使用同一个元素标签
                    String time = new String();

                    int location_Index = 0;
                    for(WebElement atselement : authors_and_time)
                    {
                        if (location_Index == authors_and_time.size() - 1)
                        {
                            time += atselement.getText();
                            break;
                        }
                        author += atselement.getText();
                        location_Index ++;
                    }
                    // 如果无法获取到时间信息
                    if(time.length() >0 && author.length() ==0)
                    {
                        author += time;// 获取不到时间信息的情况，time字符串会保存最后一个作者信息
                        time = aElement.findElement(By.className("t-ML6")).getText();
                    }
                    timeList.add(time);// 添加
                    authorList.add(author);
                    
                    // 获取摘要
                    String ab = aElement.findElement(By.className("abstract-area")).getText();
                    abstractList.add(ab);
                }
                
            } 
            catch (Exception e) 
            {
                // TODO: handle exception
                driver.close();
                
                e.printStackTrace();

            }
            
            try 
            {
                for(int i = 0; i < authorList.size(); i++)
                {
                    Thread.sleep(100);
                    // 从列表中获取当前论文信息
                    
                    String cur_title = titleList.get(i);
                    String cur_author = authorList.get(i);
                    String cur_time = new String("");
                    String cur_hrefValue = new String("");
                    String cur_abstracts = abstractList.get(i);
                    // 总论文数+1
                    total_numberOf_papers +=1;
                    // 保存到数据库
                    saveToDatabase(total_numberOf_papers, cur_title, cur_author, cur_time,cur_abstracts, cur_hrefValue, 1);
                    
                    // 保存成功并输出
                    System.out.println("\ntitle:"+cur_title);  
                    System.out.format("paper %d load successfully!\n", total_numberOf_papers);
                    
                }
                
            } 
            catch (Exception e) 
            {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
        // 5.退出浏览器
        driver.quit();
        System.out.println("\nSpider end~");
    }
}
