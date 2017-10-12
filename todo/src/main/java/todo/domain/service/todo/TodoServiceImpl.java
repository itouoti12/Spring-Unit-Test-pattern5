package todo.domain.service.todo;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.terasoluna.gfw.common.exception.BusinessException;
import org.terasoluna.gfw.common.exception.ResourceNotFoundException;
import org.terasoluna.gfw.common.message.ResultMessage;
import org.terasoluna.gfw.common.message.ResultMessages;

import todo.domain.model.Todo;
import todo.domain.repository.todo.TodoRepository;

@Service
@Transactional
public class TodoServiceImpl implements TodoService {

	private static final long MAX_UNFINISHED_COUNT = 5;

	@Inject
	TodoRepository todoRepository;

	public Todo findOne(String todoId) {
		Todo todo = todoRepository.findOne(todoId);
		if (todo == null) {
			ResultMessages messages = ResultMessages.error();
			messages.add(ResultMessage.fromText("[E004]The requested Todo is not found. (id=" + todoId + ")"));

			throw new ResourceNotFoundException(messages);
		}
		return todo;
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<Todo> findAll() {
		// TODO 自動生成されたメソッド・スタブ
		return todoRepository.findAll();
	}

	@Override
	public Todo create(Todo todo) {
		// TODO 自動生成されたメソッド・スタブ
		long unfinishedCount = todoRepository.countByFinished(false);
		if (unfinishedCount >= MAX_UNFINISHED_COUNT) {
			ResultMessages messages = ResultMessages.error();
			messages.add(ResultMessage
					.fromText("[E001]The count of un-finished Todo must not be over " + MAX_UNFINISHED_COUNT + "."));

			throw new BusinessException(messages);
		}

		String todoId = UUID.randomUUID().toString();
		Date createdAt = new Date();

		todo.setTodoId(todoId);
		todo.setCreatedAt(createdAt);
		todo.setFinished(false);

		todoRepository.create(todo);

		return todo;
	}

	@Override
	public Todo finish(String todoId) {
		// TODO 自動生成されたメソッド・スタブ
		Todo todo = findOne(todoId);
		if (todo.isFinished()) {
			ResultMessages messages = ResultMessages.error();
			messages.add(ResultMessage.fromText("[E002]The requested Todo is already finished. (id=" + todoId + ")"));

			throw new BusinessException(messages);
		}
		todo.setFinished(true);
		todoRepository.update(todo);
		return todo;
	}

	@Override
	public void delete(String todoId) {
		// TODO 自動生成されたメソッド・スタブ
		Todo todo = findOne(todoId);
		todoRepository.delete(todo);
	}

}
