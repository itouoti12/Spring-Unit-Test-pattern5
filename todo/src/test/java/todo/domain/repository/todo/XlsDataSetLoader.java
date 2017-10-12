package todo.domain.repository.todo;

import java.io.InputStream;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.springframework.core.io.Resource;

import com.github.springtestdbunit.dataset.AbstractDataSetLoader;

//AbstractDataSetLoaderクラスを継承する
public class XlsDataSetLoader extends AbstractDataSetLoader {

	//標準のxml形式からxlsl形式のフォーマットを読み込む設定に変更（override）する
	@Override
	protected IDataSet createDataSet(Resource resource) throws Exception {
		// TODO 自動生成されたメソッド・スタブ
		try(InputStream inputStream = resource.getInputStream()) {
			return new XlsDataSet(inputStream);
		}
	}
}
