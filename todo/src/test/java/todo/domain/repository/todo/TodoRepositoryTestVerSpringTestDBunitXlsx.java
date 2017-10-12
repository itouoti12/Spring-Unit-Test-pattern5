package todo.domain.repository.todo;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import todo.domain.model.Todo;

/**
 * Repository Test
 * spring-test-dbunitによるデータのセットアップ、比較
 * xlsx形式のファイルを読み込む設定
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/test-context-dbunit.xml"})
//@TestExecutionListeners・・・TestContextManagerに指定したListenerを設定する。
//正直あまり良くわかってない。spring-test-dbunitを使う時はお決まり的なやつらしい。
//TransactionDbUnitTestExecutionListenerがspring-test-dbunitで必要だからかも。
@TestExecutionListeners({
	DependencyInjectionTestExecutionListener.class,
	DirtiesContextTestExecutionListener.class,
	TransactionDbUnitTestExecutionListener.class,
	SqlScriptsTestExecutionListener.class
})
//@DbUnitConfiguration・・・自作したDataSetLoaderを使う時にこのアノテーションで設定する。
//xlsxファイルを読み込ませるようにしたDataSetLoaderを自作したため設定。
@DbUnitConfiguration(dataSetLoader = XlsDataSetLoader.class)
@Transactional
public class TodoRepositoryTestVerSpringTestDBunitXlsx {

	
	@Inject
	TodoRepository target;
	
	@Inject
	JdbcTemplate jdbctemplate;
	
	@Before
	public void setUp() {
		//spring-test-dbunitアノテーションでセットアップと比較を行うため、処理なし
	}
	
	@Test
	//@DatabaseSetup・・・spring-test-dbuniライブラリのアノテーション。テスト実行前にデータをセットアップしてくれる。テストクラスごと、メソッドごとでの指定が可能
	@DatabaseSetup("classpath:META-INF/dbunit/test_data.xlsx")
	//@ExpectedDatabase・・・spring-test-dbuniライブラリのアノテーション。テストメソッド実行後のテーブルの状態を指定したファイルと比較検証してくれる。
	//エラーの時はJunitの例のバーが赤くなる。
	@ExpectedDatabase(value="classpath:META-INF/dbunit/compare_data.xlsx", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void testUpdate() {
		//テスト用のデータを作成（getTodoDataメソッドはDBからデータを取得するprivateメソッド。取得したデータを書き換えて更新する。）
		String todoId = "cceae402-c5b1-440f-bae2-7bee19dc17fb";
		Todo testDataTodo = getTodoData(todoId);
		testDataTodo.setFinished(true);
		
		//updateメソッドのテスト
		boolean actTodo = target.update(testDataTodo);
		
		//結果検証
		assertEquals(actTodo, true);
		
	}
	
	//テスト用元データの取得
	private Todo getTodoData(String todoId) {
		
		String sql = "SELECT * FROM todo WHERE todo_id=?";
		
		Todo todoData = (Todo)jdbctemplate.queryForObject(sql, new Object[] {todoId},
				new RowMapper<Todo>() {
					public Todo mapRow(ResultSet rs, int rownum) throws SQLException {
						Todo todoSql = new Todo();
						
						todoSql.setTodoId(rs.getString("todo_id"));
						todoSql.setTodoTitle(rs.getString("todo_title"));
						todoSql.setFinished(rs.getBoolean("finished"));
						todoSql.setCreatedAt(rs.getTimestamp("created_at"));
					
						return todoSql;
					}
		});
		return todoData;
	}
}
