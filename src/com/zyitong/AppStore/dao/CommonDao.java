package com.zyitong.AppStore.dao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.SimpleTimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import android.annotation.SuppressLint;

import com.zyitong.AppStore.tools.AppLogger;
import com.zyitong.AppStore.tools.CommonConstant;

public class CommonDao {
	
	private final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	public static final HashMap<String, String> errCodeMap = new HashMap<String, String>();
	static {
		errCodeMap.put("1000", "系统内部错误");
		errCodeMap.put("1001", "没有找到模版");
		errCodeMap.put("1003", "不支持的索引类型");
		errCodeMap.put("1004", "服务暂时不可用，请稍后再试");
		errCodeMap.put("2001", "待查应用不存在");
		errCodeMap.put("2002", "应用已经存在");
		errCodeMap.put("2003", "到达创建应用总限制");
		errCodeMap.put("2004", "应用名不可用。应用名由数字、26个英文字母或下划线组成，长度不超过30位");
		errCodeMap.put("2005", "应用名称没有设定");
		errCodeMap.put("2006", "新应用名称没有设定");
		errCodeMap.put("2007", "备注不超300字");
		errCodeMap.put("2008", "摘要配置参数错误");
		errCodeMap.put("2009", "更新状态失败");
		errCodeMap.put("2010", "应用暂停中");
		errCodeMap.put("2011", "应用冻结中");
		errCodeMap.put("2012", "应用未开启");
		errCodeMap.put("2013", "删除失败，没有此应用");
		errCodeMap.put("2014", "文件上传失败");
		errCodeMap.put("2016", "区域信息没有");
		errCodeMap.put("2017", "此应用并不属于当前区域");
		errCodeMap.put("2099", "当前接口暂时不提供服务。");
		errCodeMap.put("2101", "表达式不存在");
		errCodeMap.put("2102", "表达式名称被占用");
		errCodeMap.put("2103", "到达该应用表达式总数限制");
		errCodeMap.put("2104", "表达式名不可用。表达式名由数字、26个英文字母或下划线组成，长度不超过30位");
		errCodeMap.put("2105", "表达式名称没有设定");
		errCodeMap.put("2106", "新表达式名称没有设定");
		errCodeMap.put("2107", "表达式备注不超过300字");
		errCodeMap.put("2108", "表达式备注格式错误");
		errCodeMap.put("2109", "表达式格式错误");
		errCodeMap.put("2110", "表达式长度超过限制");
		errCodeMap.put("2111", "表达式id未指定");
		errCodeMap.put("2112", "表达式错误");
		errCodeMap.put("2113", "表达式不能为空");
		errCodeMap.put("2114", "操作错误");
		errCodeMap.put("2201", "粗排配置名没有设定");
		errCodeMap.put("2202", "粗排配置名已经存在");
		errCodeMap.put("2203", "粗排配置个数超出限制");
		errCodeMap.put("2204", "粗排配置名错误。只能由数字、26个英文字母或下划线组成");
		errCodeMap.put("2205", "粗排配置名长度超出限制");
		errCodeMap.put("2206", "粗排字段必须是数值型");
		errCodeMap.put("2207", "粗排配置不存在");
		errCodeMap.put("2208", "粗排配置错误，必须包含字段");
		errCodeMap.put("2209", "粗排配置权重错误,必须是-100000到100000之间的非0数值，浮点数精度支持6位");
		errCodeMap.put("2210", "与系统默认粗排配置重名");
		errCodeMap.put("2211", "timeliness()的参数必须是INT类型");
		errCodeMap.put("2112", "排序表达式错误");
		errCodeMap.put("3001", "文档不能为空");
		errCodeMap.put("3002", "文档大小超过限制");
		errCodeMap.put("3003", "已经到最大文档数");
		errCodeMap.put("3004", "保存文档失败");
		errCodeMap.put("3005", "doc格式错误");
		errCodeMap.put("3006", "文档操作cmd不合法");
		errCodeMap.put("3007", "请求过于频繁");
		errCodeMap.put("3008", "文档总长度太长");
		errCodeMap.put("3009", "没有文档id");
		errCodeMap.put("4001", "认证失败");
		errCodeMap.put("4002", "需要设置签名");
		errCodeMap.put("4003", "签名验证失败");
		errCodeMap.put("4004", "需要设置SignatureNonce");
		errCodeMap.put("4005", "SignatureNonce不能重复使用");
		errCodeMap.put("4006", "SignatureNonce验证失败");
		errCodeMap.put("4007", "解析JSON格式失败");
		errCodeMap.put("4008", "用户名称不能为空，请检查域名正确性");
		errCodeMap.put("4009", "需要指定用户标识");
		errCodeMap.put("4010", "时间过期");
		errCodeMap.put("4011", "demo帐号禁止执行的操作");
		errCodeMap.put("4012", "数据表不存在");
		errCodeMap.put("4013", "Timestamp格式错误");
		errCodeMap.put("4014", "需要设置Timestamp");
		errCodeMap.put("5001", "用户不存在");
		errCodeMap.put("5002", "用户名不正确");
		errCodeMap.put("5003", "需要用户登录");
		errCodeMap.put("5008", "用户没有启用ACCESSKEY");
		errCodeMap.put("5100", "用户没有此区域的操作权限");
		errCodeMap.put("6001", "查询query为空");
		errCodeMap.put("6002", "并不被支持的搜索key关键字");
		errCodeMap.put("6003", "并不被支持的搜索field关键字");
		errCodeMap.put("6004", "复杂查询为空");
		errCodeMap.put("6005", "field无效");
		errCodeMap.put("6006", "请求包含太多应用名");
		errCodeMap.put("6007", "超出多索引查询每个模板中索引总数");
		errCodeMap.put("6008", "请求串语法错误，解析失败");
		errCodeMap.put("6009", "查询子句过长");
		errCodeMap.put("6010", "无效的rerank");
		errCodeMap.put("6011", "SignatureNonce格式错误");
		errCodeMap.put("6013", "start+hit超过系统限制");
		errCodeMap.put("6014", "因系统繁忙，请求被丢弃");
		errCodeMap.put("6015", "因流量超出配额，请求被丢弃");
		errCodeMap.put("6016", "查询hit数超过系统限制");
		errCodeMap.put("6100", "查询词为空");
		errCodeMap.put("6101", "查询的索引字段不存在");
		errCodeMap.put("6102", "Query中的数值范围错误");
		errCodeMap.put("6103", "Filter中的表达式返回值必须为bool类型");
		errCodeMap.put("6104", "Sort中的表达式返回值不能为bool类型");
		errCodeMap.put("6105", "Sort中存在相同的表达式");
		errCodeMap.put("6106", "查询query语句非法");
		errCodeMap.put("6107", "统计函数表达式的返回值不能为bool或者string类型");
		errCodeMap.put("6108", "统计中的范围必须为升序");
		errCodeMap.put("6109", "统计中的范围表达式返回值类型错误");
		errCodeMap.put("6110", "统计函数不存在");
		errCodeMap.put("6111", "不支持的统计函数");
		errCodeMap.put("6112", "Query");
		errCodeMap.put("6113", "Filter子句错误");
		errCodeMap.put("6114", "Aggregate子句错误");
		errCodeMap.put("6115", "Sort子句错误");
		errCodeMap.put("6116", "Distinct子句错误");
		errCodeMap.put("6117", "查询中包含未知的子句");
		errCodeMap.put("6118", "语法错误");
		errCodeMap.put("6119", "Distinct子句中的dist_count值错误，应该为大于0的整数");
		errCodeMap.put("6120", "Distinct子句中的dist_times值错误，应该为大于0的整数");
		errCodeMap.put("6121", "Distinct子句中的reserved值错误，应为true/false");
		errCodeMap.put("6122", "Distinct子句缺少distinct_key");
		errCodeMap.put("6123", "Distinct子句中的grade值错误，例如为空，或非数值");
		errCodeMap.put("6124", "Distinct子句中包含distinct个数不对,个数应在(0,2]");
		errCodeMap.put("6125", "Distinct子句中的max_item_count值错误，应该为大于0的整数");
		errCodeMap.put("6126", "Distinct子句中的update_total_hit值错误，应为true/false");
		errCodeMap.put("6127", "请求中包含了未定义的attribute字段");
		errCodeMap.put("6128", "表达式中的二元操作符的两边的表达式结果类型不匹配");
		errCodeMap.put("6129", "表达式中的二元操作符的两边表达式不能同时为常量");
		errCodeMap.put("6130", "二元逻辑运算表达式类型错误，应为bool类型");
		errCodeMap.put("6131", "二元表达式中不支持string类型");
		errCodeMap.put("6132", "二元表达式中不支持数组类型");
		errCodeMap.put("6133", "位操作中的类型错误");
		errCodeMap.put("6134", "常量表达式的返回值类型错误");
		errCodeMap.put("6300", "常量表达式类型应是整数或浮点数");
		errCodeMap.put("6301", "位取反操作数类型必须为整数");
		errCodeMap.put("6302", "取负数操作数必须为数值");
		errCodeMap.put("6303", "逻辑非操作数必须为数值");
		errCodeMap.put("6304", "二元运算操作数类型错误");
		errCodeMap.put("6305", "非法的二元运算符");
		errCodeMap.put("6306", "函数参数类型错误");
		errCodeMap.put("6307", "函数未定义");
		errCodeMap.put("6308", "函数参数个数错误");
		errCodeMap.put("6309", "非法的数组操作");
		errCodeMap.put("6310", "可过滤字段不存在");
		errCodeMap.put("6311", "数组字段被错当作单值使用");
		errCodeMap.put("6312", "单值字段被错当作数组使用");
		errCodeMap.put("6313", "数组字段下标越界(小于0)");
		errCodeMap.put("6314", "不支持的字段类型");
		errCodeMap.put("6315", "索引字段参数不存在");
		errCodeMap.put("6316", "Query中没有指定索引");
		errCodeMap.put("6317", "Filter子句中只能使用一次公式");
		errCodeMap.put("6318", "公式语法解析出错");
		errCodeMap.put("6500", "搜索语法中包含不存在的字段");
		errCodeMap.put("6501", "在线系统没有索引数据");
		errCodeMap.put("6502", "用户query语法错误");
		errCodeMap.put("7100", "没有错误发生");
		errCodeMap.put("7101", "单个文档过长");
		errCodeMap.put("7102", "文档所属应用的元信息错误（clientid、应用名或表名等不正确）");
		errCodeMap.put("7103", "HA3");
		errCodeMap.put("7104", "JSON文档格式错误：字段解析失败");
		errCodeMap.put("7105", "JSON");
		errCodeMap.put("7106", "JSON");
		errCodeMap.put("7107", "不支持的编码");
		errCodeMap.put("7108", "编码转换失败");
		errCodeMap.put("7109", "fields中没有id字段");
		errCodeMap.put("7110", "fields中id定义不合法");
		errCodeMap.put("7111", "fields中包含保留字段");
		errCodeMap.put("7201", "HA3");
		errCodeMap.put("7202", "JSON");
		errCodeMap.put("7301", "主键字段不存在");
		errCodeMap.put("7302", "字段数据类型错误");
		errCodeMap.put("7303", "数组字段相关错误");
		errCodeMap.put("7401", "文档总数超出配额");
		errCodeMap.put("7402", "每日更新文档数超出配额");
		errCodeMap.put("7403", "单次导入的数据大小超出配额");
		errCodeMap.put("7500", "系统内部错误");
		errCodeMap.put("7501", "云梯Hive待同步字段的列号超出了当前数据的列数范围");
		errCodeMap.put("7502", "从Mysql中读取到的主键字段为空,请联系数据库管理员");
		errCodeMap.put("7503", "JsonKeyValueExtractor内容转换错误:");
		errCodeMap.put("7504", "JsonKeyValueExtractor内容转换错误:");
		errCodeMap.put("7505", "TairLDBExtractor内容转换错误:");
		errCodeMap.put("7506", "TairLDBExtractor内容转换错误:");
		errCodeMap.put("7507", "MySql实时同步过滤条件格式错误");
		errCodeMap.put("7508", "系统内部错误:");
		errCodeMap.put("7509", "TairLDBExtractor内容转换配置错误：Tair连接失败，请检查configId");
		errCodeMap.put("7510", "KVExtractor内容解析错误：KV格式无法解析");
		errCodeMap.put("7511", "OSS");
		errCodeMap.put("7512", "OSS");
		errCodeMap.put("7513", "OSS");
		errCodeMap.put("7514", "系统内部错误:");
		errCodeMap.put("7515", "过滤条件执行错误");
		errCodeMap.put("7516", "字段映射过程中源表字段缺失");
		errCodeMap.put("7517", "StringCatenateExtractor内容转换错误:");
		errCodeMap.put("7518", "StringCatenateExtractor内容转换错误:");
		errCodeMap.put("7601", "任务执行错误");
		errCodeMap.put("8001", "保存错误信息失败");
		errCodeMap.put("8002", "必要参数缺失");
		errCodeMap.put("8003", "应用不存在");
		errCodeMap.put("8004", "参数错误");
		errCodeMap.put("9001", "用户名为空");
		errCodeMap.put("9002", "应用名为空");
		errCodeMap.put("9003", "模板名不可用。模板名只能由数字、26个英文字母或下划线组成");
		errCodeMap.put("9004", "模板名长度不可超过30位");
		errCodeMap.put("9005", "查询模板信息出错");
		errCodeMap.put("9006", "模板名字已存在");
		errCodeMap.put("9007", "插入模板信息出错");
		errCodeMap.put("9008", "无效的数据");
		errCodeMap.put("9009", "定义的字段数目超过系统允许的最大字段数");
		errCodeMap.put("9010", "此字段保留字段名");
		errCodeMap.put("9011", "字段已存在");
		errCodeMap
				.put("9012",
						"索引名称必须以字母开头，由数字、26个英文字母或下划线组成，长度不超过30位，多值字段类型不能为SWS_TEXT或TEXT");
		errCodeMap.put("9013", "不支持数组");
		errCodeMap.put("9014", "不支持主键");
		errCodeMap.put("9015", "未设定主键");
		errCodeMap.put("9016", "主键不唯一");
		errCodeMap.put("9017", "更新信息失败");
		errCodeMap.put("9018", "删除信息失败");
		errCodeMap.put("9019", "包含多个索引字段的搜索字段最多4个");
		errCodeMap.put("9020", "同一个STRING/TEXT类型的索引字段不能进入多个只包含一个字段的搜索字段中");
		errCodeMap.put("9021", "索引名称必须以字母开头，由数字、26个英文字母或下划线组成，长度不超过30个");
		errCodeMap.put("9022", "该表已经关联");
		errCodeMap.put("9023", "索引名不能包含多类型的字段");
		errCodeMap.put("9100", "系统内部错误");
		errCodeMap.put("9101", "该字段超过数量限制");
		errCodeMap.put("9102", "该数据源未被用到");
		errCodeMap.put("9103", "无效的外表连接");
		errCodeMap.put("9104", "最多2级关联");
		errCodeMap.put("9105", "待查模板不存在");
		errCodeMap.put("9501", "用户名为空");
		errCodeMap.put("9502", "应用名为空");
		errCodeMap.put("9519", "未指定模板");
		errCodeMap.put("9600", "系统内部错误");
		errCodeMap.put("9902", "插件字段类型错误");
		errCodeMap.put("9999", "此域名不提供本服务");
		errCodeMap.put("10001", "没有指定的tddl");
		errCodeMap.put("10002", "获取字段失败或者表不存在");
		errCodeMap.put("10011", "连接agg失败");
		errCodeMap.put("10012", "应用里存在doc");
		errCodeMap.put("10110", "该任务已结束");
		errCodeMap.put("10010", "部分数据源有问题，已经忽略有错误的数据");
		errCodeMap.put("10014", "数据源类型错误");
		errCodeMap.put("10100", "创建任务失败，未结束的任务已经存在");
		errCodeMap.put("10101", "没有指定应用ID");
		errCodeMap.put("10106", "没有指定应用ID");
		errCodeMap.put("10107", "没有指定应用ID");
		errCodeMap.put("10102", "ACTION无效");
		errCodeMap.put("10112", "文档数量超过限制");
		errCodeMap.put("10201", "获取配额列表失败");
		errCodeMap.put("10202", "更新配额失败");
		errCodeMap.put("10301", "参数错误：参数未提供或者格式不正确");
		errCodeMap.put("10302", "时间参数错误");
		errCodeMap.put("10303", "数据源未配置");
		errCodeMap.put("10304", "该表配额超限");
		errCodeMap.put("10305", "OSS参数错误");
		errCodeMap.put("10306", "OSS");
		errCodeMap.put("10307", "OSS");
		errCodeMap.put("10308", "OSS");
		errCodeMap.put("10309", "存在未完成的任务");
		errCodeMap.put("10310", "不是运行中的应用，无法创建任务");
		errCodeMap.put("10311", "时间范围不合法");
		errCodeMap.put("10312", "应用描述长度超过限制，最多600字");
		errCodeMap.put("10313", "OSS");
		errCodeMap.put("10314", "OSS");
		errCodeMap.put("10315", "OSS");
		errCodeMap.put("10330", "数据源参数不合法");
		errCodeMap.put("10350", "连接ODPS服务失败");
		errCodeMap.put("10351", "ODPS");
		errCodeMap.put("10400", "OSS前缀不合法");
		errCodeMap.put("10450", "字段不存在");
	}

	public String genSignatureNonce() {
		String randomCode = "";
		for (int i = 0; i < 4; i++) {
			int num = ((int) (0 + Math.random() * 10));
			randomCode += String.valueOf(num);
		}
		String signatureNonce = String.valueOf(System.currentTimeMillis())
				+ randomCode;

		AppLogger.i("== signatureNonce: " + signatureNonce);

		return signatureNonce;
	}

	@SuppressLint("SimpleDateFormat")
	public String genTimestamp() {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"YYYY-MM-DDThh:mm:ssZ");
		Date curDate = new Date(System.currentTimeMillis());
		String dateString = formatter.format(curDate);

		AppLogger.i("== dateString: " + dateString);

		return dateString;
	}

	protected String percentEncode(String value)
			throws UnsupportedEncodingException {
		return value != null ? URLEncoder.encode(value, "UTF-8")
				.replace("+", "%20").replace("*", "%2A").replace("%7E", "~")
				: null;
	}

	private String buildQuery(TreeMap<String, String> sortMap) {

		StringBuilder query = new StringBuilder();
		try {
			for (Entry<String, String> entry : sortMap.entrySet()) {
				query.append("&").append(percentEncode(entry.getKey()))
						.append("=").append(percentEncode(entry.getValue()));

			}
		} catch (UnsupportedEncodingException e) {
			// ignore
		}

		return query.substring(1);
	}

	protected String getAliyunSign(TreeMap<String, String> sortMap) {

		try {
			String stringToSign = buildQuery(sortMap);
			stringToSign = "GET" + "&%2F&" + percentEncode(stringToSign);

			final String ALGORITHM = "HmacSHA1";
			final String ENCODING = "UTF-8";

			String accessKeySecret = CommonConstant.ACCESS_KEY_SECRET + "&";
			Mac mac = Mac.getInstance(ALGORITHM);
			mac.init(new SecretKeySpec(accessKeySecret.getBytes(ENCODING),
					ALGORITHM));
			byte[] signData = mac.doFinal(stringToSign.getBytes(ENCODING));

			String signature = new String(Base64.encodeBase64(signData));
			//String signature = new String(Base64.encode(signData, 0));

			return signature;
		} catch (Exception e) {
			AppLogger.e("getAliyunSign Exception");
			return null;
		}
	}

	protected String formatIso8601Date(Date date) {
		SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT);
		df.setTimeZone(new SimpleTimeZone(0, "GMT"));
		return df.format(date);
	}
}
