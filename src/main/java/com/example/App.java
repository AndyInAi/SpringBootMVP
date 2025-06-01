package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@SpringBootApplication
public class App {

	private static final Logger log = LoggerFactory.getLogger(App.class); // 日志

	/**
	 * 入口
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		SpringApplication.run(App.class, args);

	}

	@Autowired
	JdbcTemplate jdbcTemplate; // 数据库连接

	/**
	 * 增加
	 *
	 * @param req
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/add", produces = "application/json;charset=UTF-8")
	public String add(HttpServletRequest req) {

		JSONObject json = new JSONObject();

		json.put("result", false);

		String first_name = req.getParameter("first_name"); // 姓

		String last_name = req.getParameter("last_name"); // 名

		if (first_name == null || last_name == null) {

			return json.toString();

		}

		first_name = first_name.trim();

		last_name = last_name.trim();

		if (first_name.length() == 0 || last_name.length() == 0) {

			return json.toString();

		}

		String[] name = new String[] { first_name, last_name };

		List<Object[]> customers = new ArrayList<>();

		customers.add(name);

		try {

			jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", customers);

		} catch (Exception ex) {

			log.debug(ex.getLocalizedMessage());

			return json.toString();

		}

		json.put("result", true);

		return json.toString();

	}

	/**
	 * 删除
	 *
	 * @param update
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/del", produces = "application/json;charset=UTF-8")
	public String del(HttpServletRequest req) {

		JSONObject json = new JSONObject();

		json.put("result", false);

		String id = req.getParameter("id");

		if (id == null) {

			return json.toString();

		}

		id = id.trim();

		if (!id.matches("\\d+")) {

			return json.toString();

		}

		try {

			jdbcTemplate.update("DELETE FROM customers WHERE id=?", id);

		} catch (Exception ex) {

			log.debug(ex.getLocalizedMessage());

			return json.toString();

		}

		json.put("result", true);

		return json.toString();

	}

	/**
	 * 初始化，建表，或删表重建
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/init", produces = "application/json;charset=UTF-8")
	public String init() {

		JSONObject json = new JSONObject();

		json.put("result", false);

		try {

			jdbcTemplate.execute("DROP TABLE IF EXISTS customers");

			jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS customers(id SERIAL, first_name VARCHAR(100), last_name VARCHAR(100))");

		} catch (Exception ex) {

			log.debug(ex.getLocalizedMessage());

			return json.toString();

		}

		json.put("result", true);

		return json.toString();

	}

	/**
	 * 查询
	 *
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/query", produces = "application/json;charset=UTF-8")
	public String query(HttpServletRequest request) {

		String first_name = request.getParameter("first_name");

		JSONObject json = new JSONObject();

		json.put("result", false);

		if (first_name == null) {

			first_name = "";

		} else {

			first_name = first_name.trim();

		}

		List<Map<String, Object>> rows = null;

		try {

			if (first_name.length() == 0) {

				rows = jdbcTemplate.queryForList("SELECT id, first_name, last_name FROM customers ORDER BY first_name");

			} else {

				rows = jdbcTemplate.queryForList("SELECT id, first_name, last_name FROM customers WHERE first_name = ? ", first_name);

			}

		} catch (Exception ex) {

			log.debug(ex.getLocalizedMessage());

			return json.toString();

		}

		json.put("result", true);

		JSONArray customers = new JSONArray(rows);

		json.put("customers", customers);

		return json.toString();

	}

	/**
	 * 测试
	 *
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/test", produces = "application/json;charset=UTF-8")
	String test() {

		JSONObject json = new JSONObject();

		json.put("Hello", "World");

		return json.toString();

	}

	/**
	 * 修改
	 *
	 * @param update
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/update", produces = "application/json;charset=UTF-8")
	public String update(HttpServletRequest req) {

		JSONObject json = new JSONObject();

		json.put("result", false);

		String id = req.getParameter("id");

		String first_name = req.getParameter("first_name");

		String last_name = req.getParameter("last_name");

		if (id == null || first_name == null || last_name == null) {

			return json.toString();

		}

		id = id.trim();

		first_name = first_name.trim();

		last_name = last_name.trim();

		if (!id.matches("\\d+") || first_name.length() == 0 || last_name.length() == 0) {

			return json.toString();

		}

		try {

			jdbcTemplate.update("UPDATE customers SET first_name=?, last_name=? WHERE id=?", first_name, last_name, id);

		} catch (Exception ex) {

			log.debug(ex.getLocalizedMessage());

			return json.toString();

		}

		json.put("result", true);

		return json.toString();

	}

}
