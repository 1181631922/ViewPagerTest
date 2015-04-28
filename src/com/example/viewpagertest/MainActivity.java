package com.example.viewpagertest;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.viewpagertest.MainActivity.AsyncImageLoader.ImageCallback;

/***
 * 
 * ˵����ViewPager ����СԲ�㵼��������������PagerAdapter������������������
 * Ҳ���Բ���FragmentPagerAdapter������˵��Fragment���Ը��õ���Ӧƽ����ֻ���
 * ���ҿ��Ը��õĴ������ã�������Щ�ô������һ�¾�֪���ˡ�
 * 
 * @author andylaw
 * 
 *         ����������鿴���ͣ�http://blog.csdn.net/lyc66666666666
 * 
 */

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	private ViewPager view_pager;

	private LayoutInflater inflater;

	// ͼƬ�ĵ�ַ��������Դӷ�������ȡ
	String[] urls = new String[] {

			"http://a.hiphotos.baidu.com/image/pic/item/3bf33a87e950352ad6465dad5143fbf2b2118b6b.jpg",
			"http://a.hiphotos.baidu.com/image/pic/item/c8177f3e6709c93d002077529d3df8dcd0005440.jpg",
			"http://f.hiphotos.baidu.com/image/pic/item/7aec54e736d12f2ecc3d90f84dc2d56285356869.jpg",
			"http://e.hiphotos.baidu.com/image/pic/item/9c16fdfaaf51f3de308a87fc96eef01f3a297969.jpg",
			"http://d.hiphotos.baidu.com/image/pic/item/f31fbe096b63f624b88f7e8e8544ebf81b4ca369.jpg",
			"http://h.hiphotos.baidu.com/image/pic/item/11385343fbf2b2117c2dc3c3c88065380cd78e38.jpg",
			"http://c.hiphotos.baidu.com/image/pic/item/3801213fb80e7bec5ed8456c2d2eb9389b506b38.jpg"

	};

	private ImageView image;
	private View item;
	private MyAdapter adapter;
	private ImageView[] indicator_imgs = new ImageView[7];// �������ͼƬ����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		view_pager = (ViewPager) findViewById(R.id.view_pager);
		List<View> list = new ArrayList<View>();
		inflater = LayoutInflater.from(this);

		/**
		 * �������item ��ÿһ��viewPager����һ��item�� �ӷ�������ȡ�����ݣ������±��⡢url��ַ�� ��������������
		 */
		for (int i = 0; i < 7; i++) {
			item = inflater.inflate(R.layout.item, null);
			((TextView) item.findViewById(R.id.text_view)).setText("�� " + i
					+ " �� viewPager");
			list.add(item);
		}

		// ������������ ����װ���������ݽ�ȥ
		adapter = new MyAdapter(list);
		view_pager.setAdapter(adapter);

		// �󶨶������������緭ҳ�Ķ���
		view_pager.setOnPageChangeListener(new MyListener());

		initIndicator();
	}

	/**
	 * ��ʼ������ͼ�� ��̬�������СԲ�㣬Ȼ����װ�����Բ�����
	 */
	private void initIndicator() {

		ImageView imgView;
		View v = findViewById(R.id.indicator);// ����ˮƽ���֣�����̬��������ͼ��

		for (int i = 0; i < 7; i++) {
			imgView = new ImageView(this);
			LinearLayout.LayoutParams params_linear = new LinearLayout.LayoutParams(
					10, 10);
			params_linear.setMargins(7, 10, 7, 10);
			imgView.setLayoutParams(params_linear);
			indicator_imgs[i] = imgView;

			if (i == 0) { // ��ʼ����һ��Ϊѡ��״̬

				indicator_imgs[i]
						.setBackgroundResource(R.drawable.indicator_focused);
			} else {
				indicator_imgs[i].setBackgroundResource(R.drawable.indicator);
			}
			((ViewGroup) v).addView(indicator_imgs[i]);
		}

	}

	/**
	 * ������������װ�� ������ ���� �� ��� ��
	 */
	private class MyAdapter extends PagerAdapter {

		private List<View> mList;

		private AsyncImageLoader asyncImageLoader;

		public MyAdapter(List<View> list) {
			mList = list;
			asyncImageLoader = new AsyncImageLoader();
		}

		/**
		 * Return the number of views available.
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		/**
		 * Remove a page for the given position. ������������� �����ٵ�ǰҳ��ǰһ����ǰһ����ҳ��
		 * instantiateItem(View container, int position) This method was
		 * deprecated in API level . Use instantiateItem(ViewGroup, int)
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView(mList.get(position));

		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		/**
		 * Create the page for the given position.
		 */
		@Override
		public Object instantiateItem(final ViewGroup container,
				final int position) {

			Drawable cachedImage = asyncImageLoader.loadDrawable(
					urls[position], new ImageCallback() {

						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {

							View view = mList.get(position);
							image = ((ImageView) view.findViewById(R.id.image));
							image.setBackground(imageDrawable);
							container.removeView(mList.get(position));
							container.addView(mList.get(position));
							// adapter.notifyDataSetChanged();

						}
					});

			View view = mList.get(position);
			image = ((ImageView) view.findViewById(R.id.image));
			image.setBackground(cachedImage);

			container.removeView(mList.get(position));
			container.addView(mList.get(position));
			// adapter.notifyDataSetChanged();

			return mList.get(position);

		}

	}

	/**
	 * ���������������첽����ͼƬ
	 *
	 */
	private class MyListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int state) {
			// TODO Auto-generated method stub
			if (state == 0) {
				// new MyAdapter(null).notifyDataSetChanged();
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int position) {

			// �ı����е����ı���ͼƬΪ��δѡ��
			for (int i = 0; i < indicator_imgs.length; i++) {

				indicator_imgs[i].setBackgroundResource(R.drawable.indicator);

			}

			// �ı䵱ǰ����ͼƬΪ��ѡ��
			indicator_imgs[position]
					.setBackgroundResource(R.drawable.indicator_focused);
		}

	}

	/**
	 * �첽����ͼƬ
	 */
	static class AsyncImageLoader {

		// �����ã�ʹ���ڴ�����ʱ���� �������˳������ڴ治������������ã�
		private HashMap<String, SoftReference<Drawable>> imageCache;

		public AsyncImageLoader() {
			imageCache = new HashMap<String, SoftReference<Drawable>>();
		}

		/**
		 * ����ص��ӿ�
		 */
		public interface ImageCallback {
			public void imageLoaded(Drawable imageDrawable, String imageUrl);
		}

		/**
		 * �������̼߳���ͼƬ ���̼߳�����ͼƬ����handler�������̲߳��ܸ���ui����handler�������̣߳����Ը���ui��
		 * handler�ֽ���imageCallback��imageCallback��Ҫ�Լ���ʵ�֣���������ԶԻص��������д���
		 *
		 * @param imageUrl
		 *            ����Ҫ���ص�ͼƬurl
		 * @param imageCallback
		 *            ��
		 * @return
		 */
		public Drawable loadDrawable(final String imageUrl,
				final ImageCallback imageCallback) {

			// ��������д���ͼƬ ��������ʹ�û���
			if (imageCache.containsKey(imageUrl)) {
				SoftReference<Drawable> softReference = imageCache
						.get(imageUrl);
				Drawable drawable = softReference.get();
				if (drawable != null) {
					imageCallback.imageLoaded(drawable, imageUrl);// ִ�лص�
					return drawable;
				}
			}

			/**
			 * �����߳���ִ�лص���������ͼ
			 */
			final Handler handler = new Handler() {
				public void handleMessage(Message message) {
					imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
				}
			};

			/**
			 * �������̷߳������粢����ͼƬ ���ѽ������handler����
			 */
			new Thread() {
				@Override
				public void run() {
					Drawable drawable = loadImageFromUrl(imageUrl);
					// �������ͼƬ�ŵ�������
					imageCache.put(imageUrl, new SoftReference<Drawable>(
							drawable));
					Message message = handler.obtainMessage(0, drawable);
					handler.sendMessage(message);
				}
			}.start();

			return null;
		}

		/**
		 * ����ͼƬ ��ע��HttpClient ��httpUrlConnection������
		 */
		public Drawable loadImageFromUrl(String url) {

			try {
				HttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 1000 * 15);
				HttpGet get = new HttpGet(url);
				HttpResponse response;

				response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = response.getEntity();

					Drawable d = Drawable.createFromStream(entity.getContent(),
							"src");

					return d;
				} else {
					return null;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		// �������
		public void clearCache() {

			if (this.imageCache.size() > 0) {

				this.imageCache.clear();
			}

		}

	}

}
