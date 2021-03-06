package jmh.benchmarks.http;

import net.dreamlu.mica.http.HttpRequest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * mica http 压测
 *
 * @author L.cm
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MINUTES)
@State(Scope.Thread)
public class MicaHttpBenchmark {
	private OkHttpClient httpClient = new OkHttpClient();

	@Benchmark
	public String micaHttp() {
		// 百度不同 ua 解析差别很大，故采用相同 ua，保证公平
		return HttpRequest.get("https://www.baidu.com/")
			.userAgent("okhttp/3.14.2")
			.execute()
			.asString();
	}

	@Benchmark
	public String okHttp() throws IOException {
		Request request = new Request.Builder()
			.get()
			.url("https://www.baidu.com")
			.build();
		return httpClient.newCall(request)
			.execute()
			.body()
			.string();
	}

	@Benchmark
	public String protoTypeOkHttp() throws IOException {
		Request request = new Request.Builder()
			.get()
			.url("https://www.baidu.com")
			.build();
		return new OkHttpClient().newCall(request)
			.execute()
			.body()
			.string();
	}

	public static void main(String[] args) throws RunnerException {
		Options opts = new OptionsBuilder()
			.include(MicaHttpBenchmark.class.getSimpleName())
			.warmupIterations(5)
			.measurementIterations(5)
			.jvmArgs("-server")
			.forks(1)
			.resultFormat(ResultFormatType.TEXT)
			.build();
		new Runner(opts).run();
	}
}
