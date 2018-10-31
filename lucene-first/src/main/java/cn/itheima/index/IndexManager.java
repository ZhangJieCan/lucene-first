package cn.itheima.index;

import cn.itheima.dao.BookDao;
import cn.itheima.dao.impl.BookDaoImpl;
import cn.itheima.po.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 索引管理类
 */

public class IndexManager {

    // 定义索引库目录的常量
    public static  final  String INDEX_PARH="D:\\index";

    /**
     * 索引流程实现（创建索引）
     */
    @Test
    public void createIndex() throws Exception{
//        1.采集数据
        BookDao bookDao = new BookDaoImpl();
        List<Book> bookList = bookDao.findAllBooks();

//        2.建立文档对象（Document）
        List<Document> docList = new ArrayList<Document>();
        for(Book book:bookList){
            // 创建文档对象
            Document doc = new Document();

            // 给文档对象添加域
            // 图书Id
            /**
             * add方法：给文档对象添加域
             * 参数：TextField（文本域）
             * 文本域的三个参数：
             *     参数一：域的名称
             *     参数二：域值
             *     参数三：指定是否把域值保存到文档对象中
             */
            doc.add(new TextField("bookId",book.getId()+"", Field.Store.YES));

            // 图书名称
            doc.add(new TextField("bookName",book.getBookName(), Field.Store.YES));
            // 图书价格
            doc.add(new TextField("bookPrice",book.getPrice()+"", Field.Store.YES));
            // 图书图片
            doc.add(new TextField("bookPic",book.getPic(), Field.Store.YES));
            // 图书描述
            doc.add(new TextField("bookDesc",book.getBookDesc(), Field.Store.YES));

            docList.add(doc);

        }

//        3.建立分析器对象（Analyzer），用于分词
        // Analyzer analyzer =new  StandardAnalyzer();

        // 使用ik中文分词器
        Analyzer analyzer = new IKAnalyzer();

//        4.建立索引库配置对象（IndexWriterConfig），配置索引库
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_3,analyzer);

//        5.建立索引库的目录对象（Directory），指定索引库的位置
        File file = new File("D:\\index");
        Directory directory = FSDirectory.open(file);

//        6.建立索引库的操作对象（IndexWriter），操作索引库
        IndexWriter writer = new IndexWriter(directory,iwc);

//        7.使用IndexWriter把文档对象写入索引库
        for(Document doc:docList){
            // addDocument:把文档对象写入索引库
            writer.addDocument(doc);
        }

//        8.释放资源
        writer.close();
    }

    /**
     * 检索流程实现（读取索引）
     */
    @Test
    public void readIndex() throws Exception{
//        1.建立分析器对象（Analyzer），用于分词
        //Analyzer analyzer = new StandardAnalyzer();
        // 使用ik中文分词器
        Analyzer analyzer = new IKAnalyzer();

//        2.建立查询对象（Query）
        // 2.1.建立查询解析器对象
        /**
         * 参数一：默认搜索域
         * 参数二：分析器对象
         */
        QueryParser parser = new QueryParser("bookName",analyzer);

        // 2.2.使用查询解析器对象，解析表达式，实例化Query对象
        Query query = parser.parse("bookName:java");

//        3.建立索引库目录对象（Directory），指定索引库的位置
        Directory directory  = FSDirectory.open(new File(INDEX_PARH));

//        4.建立索引库的读取对象（IndexReader），把倒排索引数据读取到内存
        IndexReader reader = DirectoryReader.open(directory);

//        5.建立索引库搜索对象（IndexSearcher），搜索索引库
        IndexSearcher searcher = new IndexSearcher(reader);

//        6.使用IndexSearcher搜索，返回搜索结果集（TopDocs）
        /**
         * search：执行搜索方法
         * 参数：
         *  参数一：查询对象
         *  参数二：指定搜素结果中排序后的前n个
         */
        TopDocs topDocs = searcher.search(query, 10);

//        7.处理结果集
        // 7.1实际搜索到的数量
        System.out.println("实际搜索到的数量："+topDocs.totalHits);

        // 7.2.取出搜索到的数据
        /**
         * ScoreDoc对象：它只有两个信息。一个是当前文档的分值；一个是当前文的id
         */
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for(ScoreDoc sd:scoreDocs){
            // 获取文档分值和id
            int docId = sd.doc;
            float score = sd.score;
            System.out.println("当前文档的id："+docId+",分值："+score);

            // 根据文档id获取文档数据
            Document doc = searcher.doc(docId);
            System.out.println("图书Id："+doc.get("bookId"));
            System.out.println("图书名称："+doc.get("bookName"));
            System.out.println("图书价格："+doc.get("bookPrice"));
            System.out.println("图书图片："+doc.get("bookPic"));
            System.out.println("图书描述："+doc.get("bookDesc"));

            System.out.println("-----------------------华丽丽分割线---------------------------");
        }

//        8.释放资源
        reader.close();
    }


    /**
     * 索引流程实现（学习Lucene的不同的Field种类）
     */
    @Test
    public void createIndexByField() throws Exception{
//        1.采集数据
        BookDao bookDao = new BookDaoImpl();
        List<Book> bookList = bookDao.findAllBooks();

//        2.建立文档对象（Document）
        List<Document> docList = new ArrayList<Document>();
        for(Book book:bookList){
            // 创建文档对象
            Document doc = new Document();

            // 给文档对象添加域
            // 图书Id
            /**
             * add方法：给文档对象添加域
             * 参数：TextField（文本域）
             * 文本域的三个参数：
             *     参数一：域的名称
             *     参数二：域值
             *     参数三：指定是否把域值保存到文档对象中
             */
            /**
             * 图书Id：
             是否分词：不需要
             是否索引：需要
             是否存储：需要

             --StringField
             */
            doc.add(new StringField("bookId",book.getId()+"", Field.Store.YES));

            // 图书名称
            /**
             * 图书名称：
             是否分词：需要
             是否索引：需要
             是否存储：需要

             --TextField
             */
            doc.add(new TextField("bookName",book.getBookName(), Field.Store.YES));
            // 图书价格
            /**
             * 图书价格：999888777
             是否分词：（Lucene针对数值型的Field，使用内部分词）
             是否索引：需要
             是否存储：需要

             --DoubleField
             */
            doc.add(new DoubleField("bookPrice",book.getPrice(), Field.Store.YES));
            // 图书图片
            /**
             * 图书图片：
             是否分词：不需要
             是否索引：不需要
             是否存储：需要

             --StoredField
             */
            doc.add(new StoredField("bookPic",book.getPic()));
            // 图书描述
            /**
             * 图书描述：
             是否分词：需要
             是否索引：需要
             是否存储：不需要

             --TextField
             */
            doc.add(new TextField("bookDesc",book.getBookDesc(), Field.Store.NO));

            docList.add(doc);

        }

//        3.建立分析器对象（Analyzer），用于分词
        // Analyzer analyzer =new  StandardAnalyzer();

        // 使用ik中文分词器
        Analyzer analyzer = new IKAnalyzer();

//        4.建立索引库配置对象（IndexWriterConfig），配置索引库
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_4_10_3,analyzer);

//        5.建立索引库的目录对象（Directory），指定索引库的位置
        File file = new File("D:\\index");
        Directory directory = FSDirectory.open(file);

//        6.建立索引库的操作对象（IndexWriter），操作索引库
        IndexWriter writer = new IndexWriter(directory,iwc);

//        7.使用IndexWriter把文档对象写入索引库
        for(Document doc:docList){
            // addDocument:把文档对象写入索引库
            writer.addDocument(doc);
        }

//        8.释放资源
        writer.close();
    }



}
