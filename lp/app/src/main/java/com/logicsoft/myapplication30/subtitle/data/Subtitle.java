//package kr.logicsoft.listeningplayer.subtitle.data;
package com.logicsoft.myapplication30.subtitle.data;

import android.content.Context;
import android.util.Log;

import com.logicsoft.myapplication30.VideoActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Observable;

//import kr.logicsoft.listeningplayer.R;
//import kr.logicsoft.listeningplayer.VideoActivity;
//import kr.logicsoft.listeningplayer.VideoControllerView;

/**
 * 占쎌��筌�占� 占쎈릭占쎄돌(占쎈릭占쎄돌占쎌�� 占쎈섧占쎈선)��占� 占쎈ご占쎌�쏙옙釉�占쎈�� 占쎄깻占쎌��占쎈��. 占쎌��占쏙옙甕곤옙 占쎈�ｏ옙苑⑼옙肉�占쎄�占쎌�� 雅��깆�� 占쎄깻占쎌��占쎈�ゅ�占�
 * 占쎈�占쎈��. 占쎌�쏙옙�� 占쎈��揶�袁⑸� 占쎈뎡占쎌�� 占쎈ご占쎈��占쎈�占쎈� 占쎈릭占쎈�� 占쎈��占쎈�わ옙��揶�占� 獄�遺억옙占� 野����� 占쎌��占쏙옙甕곌쑬諭억옙肉�野�占� 占쎈염占쎌�ワ��뱀��
 * 占쎈��깃���.
 * 
 * @author YJKim
 *
 */
public class Subtitle extends Observable implements Iterable<SyncItem>, Iterator<SyncItem> {
	// class = .KRCC, name = 占쎈립�댐옙占쎈선, lang = ko-KR
	// class = .ENCC, name = 占쎌�븝옙堉�, lang = en-US
	private static String TAG = "logicsoft";

	public static long compare_endTime;
	public static int compare_index = -1;
	public static boolean flag = false;
	public static boolean flag2 = true;
	public static int startTime;
	private int index5;
    private  int index6=0;

	public static int level=1;

	public static boolean fin = false;

	public static String realsubtitle;
    public static Context ins;

    public static class Head {
		public LanguageTag language = null;
		public String title = null;
		public Map<String, String> attributes = new HashMap<String, String>();
	}

	public static class Body implements Iterable<SyncItem>, Iterator<SyncItem> {
		public List<SyncItem> syncs = new ArrayList<SyncItem>();

		private int index = 0;

		@Override
		public boolean hasNext() {
			return index < syncs.size();
		}

		@Override
		public SyncItem next() {
			if (index >= syncs.size())
				throw new NoSuchElementException();
			return syncs.get(index++);
		}

		@Override
		public void remove() {
			syncs.remove(index);
		}

		@Override
		public Iterator<SyncItem> iterator() {
			return this;
		}
	}

	public Head head = new Head();
	public Body body = new Body();
	private int indexCache = -1;

	public interface SubtitleLevelHandler {		
		boolean askDelete(WordItem word);

		boolean askDelete(IdiomItem idiom);

		boolean askShow(WordItem word);

		boolean askShow(IdiomItem idiom);

		boolean askShowMeaning(WordItem word);

		boolean askShowMeaning(IdiomItem idiom);
	}

	
	
	private SubtitleLevelHandler levelHandler = new SubtitleLevelHandler() {
		@Override
		public boolean askShowMeaning(IdiomItem idiom) {
			switch (level) {
			case 1:
				return idiom.getLevel() == 1;
			case 2:
				return (idiom.getLevel() == 1)||(idiom.getLevel() == 2);
			case 3:
				return idiom.getLevel() >= 1;
			default:
				return false;
			}
		}

		@Override
		public boolean askShowMeaning(WordItem word) {
			switch (level) {
			case 1:
				return word.getLevel() == 1;
			case 2:
				return (word.getLevel() == 1)||(word.getLevel() == 2);
			case 3:
				return word.getLevel() >= 1;
			default:
				return false;
			}
		}

		@Override
		public boolean askShow(IdiomItem idiom) {
			switch (level) {
			case 1:
				return idiom.getLevel() == 1;
			case 2:
				return (idiom.getLevel() == 1)||(idiom.getLevel() == 2);
			case 3:
				return idiom.getLevel() >= 1;
			default:
				return false;
			}
		}

		@Override
		public boolean askShow(WordItem word) {
			switch (level) {
			case 1:
				return word.getLevel() == 1;
			case 2:
				return (word.getLevel() == 1)||(word.getLevel() == 2);
			case 3:
				return word.getLevel() >= 1;
			default:
				return false;
			}
		}

		@Override
		public boolean askDelete(IdiomItem idiom) {
			switch (level) {
			case 1:
				return false;
			case 2:
				return false;
			case 3:
				return false;
			default:
				return false;
			}
		}

		@Override
		public boolean askDelete(WordItem word) {
			switch (level) {
			case 1:
				return word.getLevel() == 1;
			case 2:
				return (word.getLevel() == 1)||(word.getLevel() == 2);
			case 3:
				return word.getLevel() >= 1;
			default:
				return false;
			}
		}
	};

	public String getCurrentExtraText() {
		SyncItem sync = getCachedItem();
		if (!isConverted())
			return "";

		Log.e(TAG, "sync: " + sync.toString() + "length: " + sync.toString().length());

		if (sync.toString().length() == index5) {
			Log.e(TAG, "sync repeated: " + sync.toString().length());
			return "";
		}
		index5 = sync.toString().length();

		StringBuilder sb = new StringBuilder();
		for (ExtraItem extra : sync.getExtraItems()) {
			if (extra instanceof WordItem) {
				WordItem word = (WordItem) extra;
				if (levelHandler != null && levelHandler.askShow(word)) {
					sb.append(word.toString(true, false, levelHandler.askShowMeaning(word))+"/");
				}
			} else if (extra instanceof IdiomItem) {
				IdiomItem idiom = (IdiomItem) extra;
				if (levelHandler != null && levelHandler.askShow(idiom)) {
					sb.append(idiom.toString(true, false, levelHandler.askShowMeaning(idiom), false) + "/");
				}
			}
		}
		/*try {
			sb.delete(sb.lastIndexOf(", "), sb.length());
		} catch (Exception e) {

		}*/

		Log.e("subtitle hello2", "sb: " +  sb.toString() + "length " + sb.length());

		return sb.toString();
		// reutn x;
	}

	public String MixCurrentExtraText() {

		Log.e("hello", "mix current extra text");

		String[] extraword;
		extraword= new String[20];
		double[] arr;
		arr= new double[20];
		
		int i=0,a;
		double temp;
		String saveExtraword;
		
		SyncItem sync = getCachedItem();

		Log.e("hello", "sync : " + sync.toString());
		if (!isConverted())
			return "";

		StringBuilder sb = new StringBuilder();
		Log.e(TAG, "string builder"  + sb.toString());
		for (ExtraItem extra : sync.getExtraItems()) {
			Log.e(TAG, "extra item: " + extra.toString());
			if (extra instanceof WordItem) {
				WordItem word = (WordItem) extra;
				if (levelHandler != null && levelHandler.askShow(word)) {
					extraword[i]=word.toString(true, false, levelHandler.askShowMeaning(word));
					i++;
				}
			} 
		}
		for (a=0;a<i;a++)	arr[a]=Math.random();
		
		for (a=0;a<i;a++) {
            for (int b=a+1;b<i;b++) {
                if(arr[a] > arr[b]) {
                    temp = arr[b];
                    arr[b] = arr[a];
                    arr[a] = temp;
                    saveExtraword=extraword[b];
                    extraword[b]=extraword[a];
                    extraword[a]=saveExtraword;
                }
            }
        }
		
		for(a=0;a<i;a++)	sb.append(extraword[a]+"/");

		Log.e("subtitle hello", "subtitle: " +  sb.toString());
		
		/*try {
			sb.delete(sb.lastIndexOf(", "), sb.length());
		} catch (Exception e) {

		}*/
		if(VideoActivity.mSubtitleCheckBox.isChecked())	return sb.toString();
		else return "";
	}
	
	public String getCurrentText() { // ��留� 異��λ�
		SyncItem sync = getCachedItem();
		Log.d(TAG, "sync in curerent context: " + sync);

		if (!isConverted())
			return sync.getText();

		realsubtitle=sync.getText();
		
		StringBuilder sb = new StringBuilder(sync.getText());
		for (int i = sync.getExtraItems().size() - 1; i >= 0; i--) {
			ExtraItem extra = sync.getExtraItems().get(i);
			if (extra instanceof WordItem && VideoActivity.mModeCheckBox.isChecked()&&!VideoActivity.FillClick) {
				WordItem word = (WordItem) extra;
				if (levelHandler != null && levelHandler.askDelete(word)) {
					sb.delete(word.getStartIndex(), word.getEndIndex());
					sb.insert(word.getStartIndex(), "(___) ");
				}
			} /*else if (extra instanceof IdiomItem) {
				IdiomItem idiom = (IdiomItem) extra;
				if (levelHandler != null && levelHandler.askDelete(idiom) && VideoControllerView.mModeCheckBox.isChecked()) {
					for (int j = idiom.getWords().size() - 1; j >= 0; j--) {
						WordItem word = idiom.getWord(j);
						sb.delete(word.getStartIndex(), word.getEndIndex());
						sb.insert(word.getStartIndex(), "_____ ");
					}
				}
			}*/
		}
		System.out.println(sb.toString());
		if(VideoActivity.mSubtitleCheckBox.isChecked())	return sb.toString();
		else return "";
	}
	

	/**
	 * 占쎌�쏙옙�� 筌�癒���占쎈� 占쎌��筌��깆�� 占쎈��占쎄� 占쎈릭占쎄돌��占� 揶�占쏙옙議�占쎌��
	 * 
	 * @return
	 */
	private SyncItem getCachedItem() {
		Log.d(TAG, "indexCache : " +indexCache + "syncItem : " + body.syncs.get(indexCache));
		return body.syncs.get(indexCache);


	}

	/**
	 * 占쎈�占쎌��占쎌�� 占쎈��揶�袁⑹�� �④쑴�� 筌ｋ��寃�占쎈릭占쎈연 占쎄땀占쎌��占쎌�� 獄�遺억옙占� 野�����, 占쎌��占쏙옙甕곌쑬諭억옙肉�野�占� 占쎈염占쎌�ワ��뱀�� 占쎈��깃���.
	 * 
	 * @param time
	 */
	public void observe(long time) {

	    Log.e(TAG, "observe:" + time + "prevclick" + VideoActivity.prevClick);
        Log.e(TAG, "flag " + flag + "flag2 " + flag2 + "index: " + index6 + "prev " + VideoActivity.prevClick);
        index6++;
		int index = getIndexAtTime(time);

		if ((compare_index > -1) && (index == -1))
			index = compare_index; // �곗���댁�� 硫�異��� 遺�遺� ����
		long endTime = getItemAtIndex(index).getEndTime();
		startTime = (int) getItemAtIndex(index).getStartTime();

		if(VideoActivity.prevClick)
		{
		    Log.e(TAG, "video activity pre click: " + VideoActivity.prevClick);


			endTime=compare_endTime;
			compare_endTime=0;
			// deleted 6-2
            // VideoActivity.prevClick=false;

		}


		String endTimeStr = Long.toString(endTime);
		String curTimeStr = Long.toString(time);

		Log.e(curTimeStr, endTimeStr);
		// fin = false;

		if (isIndexInBound(index) && index != indexCache) {
			Log.e(curTimeStr + "nostop", endTimeStr);

			indexCache = index;
			fin = false;
			setChanged();
			notifyObservers(null);
		}

        Log.i(TAG, "compare_endTime : " + compare_endTime + "end time :" + endTime + "FLAG "+ flag);
		if (flag) {
		    if (compare_endTime == endTime) {
                flag2 = false;

            } else {
				flag2 = true;
				flag = false;
			}
		}

		// else if((flag==false)&&(compare_endTime!=endTime)) endTime=endTime2;
		// //��媛� ��湲곕�� 遺�遺� ����


		if ((time > endTime - 300) && flag2 && (!getCurrentText().contains("nbsp"))) {
				
			Log.d("LogicSoft", "index : " + index);
			Log.d("LogicSoft", "indexCache : " + indexCache);

			Log.e(curTimeStr + "stop", endTimeStr);
			Log.d("LogicSoft", "endtime : " + endTime);
			Log.d("LogicSoft", "time_current : " + time);
			fin = true;
			VideoActivity.prevClick=false;
			flag = true;
			setChanged();
			notifyObservers(null);
		}
		compare_endTime = endTime;
		compare_index = index;

		/*
		 * else if(isIndexInBound(index) && index == indexCache && (time <=
		 * endTime && time > endTime-180)) { Log.d("LogicSoft", "index : "+
		 * index); Log.d("LogicSoft", "indexCache : "+ indexCache);
		 * 
		 * Log.e(curTimeStr+"stop", endTimeStr); Log.d("LogicSoft", "endtime : "
		 * + endTime); Log.d("LogicSoft", "time_current : "+ time); fin = true;
		 * setChanged(); notifyObservers(null); }
		 */
	}

	/**
	 * �ο옙 占쎈��占쎄�占쎌�� 揶�占쏙옙����占� 揶�占쏙옙議�占쎌��
	 * 
	 * @return
	 */
	public int getCount() {
		return body.syncs.size();
	}

	/**
	 * 獄�遺우�ワ옙���깍옙 筌ｋ��寃�
	 * 
	 * @param index
	 * @return 獄�遺우�ワ옙���깍옙 占쎈�占쎈� 占쎌�놂옙��筌�占� 占쎈연�븝옙
	 */
	private boolean isIndexInBound(int index) {
		if (index < 0)
			return false;
		else if (index >= getCount())
			return false;
		else
			return true;
	}

	/**
	 * 獄�遺우�ワ옙���깍옙 占쎈�占쎌�� 揶�誘⑹��占쎌�� �귐�苑�
	 * 
	 * @param index
	 * @return 0 �븝옙占쎄숲 getCount()-1 占쎄�占쎌��占쎌�� 揶�誘⑹��占쎌�� �귐�苑�
	 */
	private int getBoundedIndex(int index) {
		if (index < 0)
			return 0;
		else if (index >= getCount())
			return getCount() - 1;
		else
			return index;
	}

	/**
	 * 占쎈��占쎌�� 占쎈��揶�袁⑸�占쎄�占쎌�� 占쎈��占쎄� 占쎌�ㅿ옙�놂옙�ょ��占� 揶�占쏙옙議�占쎌��
	 * 
	 * @param time
	 * @return 占쎈��占쎄� 占쎌�ㅿ옙�놂옙��
	 */
	public int getIndexAtTime(long time) {
		// Log.d("LogicSoft", "time : "+ time);
		return binarySearch(time);
	}

	/**
	 * 占쎈��占쎌�� 占쎈��揶�袁⑸�占쎄�占쎌�� 占쎈��占쎄� 占쎈�占쎌��占쎈�ο옙�� 揶�占쏙옙議�占쎌��
	 * 
	 * @param time
	 * @return 占쎈��占쎄� 占쎈�占쎌��占쎈��
	 */
	private SyncItem getItemAtTime(long time) {
		return body.syncs.get(getBoundedIndex(getIndexAtTime(time)));
	}

	/**
	 * 占쎈��占쎌�� 占쎌�ㅿ옙�놂옙�わ옙�� 占쎈��占쎄� 占쎈�占쎌��占쎈�ο옙�� 揶�占쏙옙議�占쎌��
	 * 
	 * @param index
	 * @return 占쎈��占쎄� 占쎈�占쎌��占쎈��
	 */
	private SyncItem getItemAtIndex(int index) {
		return body.syncs.get(getBoundedIndex(index));
	}

	/**
	 * 占쎈��占쎄� 占쎈�占쎌��占쎈�� 占쎈릭占쎄돌��占� �곕떽占쏙옙釉�
	 * 
	 * @param item
	 */
	public void addSyncItem(SyncItem item) {
		body.syncs.add(item);
	}

	/**
	 * 癰�占쏙옙��占쎈� 占쎌��筌��깆�ㅿ�占� 占쎌��占쎌�ㅿ옙釉�
	 * 
	 * @return 癰�占쏙옙��占쎈� 占쎌��筌��깆��筌�占� true, 占쎈�占쎈�뀐�占� false
	 */
	public boolean isConverted() {
		return (getLanguage() == LanguageTag.CONVERTED);
	}

	/**
	 * 占쎌��筌��깆�� 占쎌��甕겸�μ�� 揶�占쏙옙議�占쎌�� (癰�占쏙옙�� 占쎌��筌�占� 占쎈�占쎈��: 0, 占쎈릭: 1, 餓ο옙: 2, 占쎄맒: 3)
	 * 
	 * @return 占쎌��筌��깆�� 占쎌��甕곤옙
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * 占쎌��筌��깆�� 占쎌��甕겸�μ�� 占쎄�占쎌��占쎈맙 (癰�占쏙옙�� 占쎌��筌�占� 占쎈�占쎈��: 0, 占쎈릭: 1, 餓ο옙: 2, 占쎄맒: 3)
	 * 
	 * @param level
	 *            占쎌��筌��깆�� 占쎌��甕곤옙
	 */
	public void setLevel(int level) {
		if (this.level != level && (level >= 0 && level <= 3)) {
			this.level = level;
			// 占쎌��筌��깆�� 占쎌��甕겸�μ�� 癰�占썲����由븝�占� 占쎌��占쏙옙甕곌쑬諭억옙肉�野�占� 占쎈르�깍옙
			setChanged();
			notifyObservers(null);
		}
	}

	/**
	 * 占쎌�� 占쎌��筌��깆�� 占쎈섧占쎈선��占� 占쎄�占쎌��占쎈맙
	 * 
	 * @param language
	 *            占쎈섧占쎈선 �얜���占쎈였 (占쎌��: 占쎈립�댐옙占쎈선, 占쎌�븝옙堉�)
	 */
	public void setLanguage(LanguageTag language) {
		head.language = language;
	}

	/**
	 * 占쎌�� 占쎌��筌��깆�� 占쎈섧占쎈선��占� 揶�占쏙옙議�占쎌��
	 * 
	 * @return 占쎈섧占쎈선 �얜���占쎈였 (占쎌��: 占쎈립�댐옙占쎈선, 占쎌�븝옙堉�)
	 */
	public LanguageTag getLanguage() {
		return head.language;
	}

	/**
	 * 占쎌�� 占쎌��筌��깆�� 占쎌�ｏ��뱀�� 揶�占쏙옙議�占쎌��
	 * 
	 * @return 占쎌�ｏ�占� �얜���占쎈였
	 */
	public String getTitle() {
		return head.title;
	}

	/**
	 * 占쎌�� 占쎌��筌��깆�� 占쎌�ｏ��뱀�� 占쎄�占쎌��占쎈맙
	 * 
	 * @param title
	 *            占쎌�ｏ�占� �얜���占쎈였
	 */
	public void setTitle(String title) {
		head.title = title;
	}

	/**
	 * 占쎌��筌��깆�� 占쎌��域뱀���� 占쎈맙, 占쎄�占쎌��占쎈┷疫뀐옙 占쎌�� 獄���諭띰옙�� 占쎌���곗��由븝옙堉깍옙鍮� 占쎈립占쎈��.
	 */
	public void normalize() {
		fillEndTime();
		sort();
	}

	/*
	 * public long prevTime(long time) {
	 * 
	 * }
	 * 
	 * public long nextTime(long time) {
	 * 
	 * }
	 */
	// Comparator-鍮�援�
	private static Comparator<SyncItem> startTimeComparator = new Comparator<SyncItem>() {
		@Override
		public int compare(SyncItem lhs, SyncItem rhs) {
			long t1 = lhs.getStartTime();
			long t2 = rhs.getStartTime();
			long cmp = t1 - t2;
			if (cmp < 0)
				return -1;
			else if (cmp > 0)
				return 1;
			else
				return 0;
		}
	};

	/**
	 * 占쎈��占쎌�� 占쎈��占쎄� 占쎈��揶�袁⑸� 占쎈뎡占쎌�� 占쎌��占쎌�뱄옙釉�
	 */
	private void sort() {
		Collections.sort(body.syncs, startTimeComparator);
	}

	/**
	 * �ル��利� 占쎈��揶�袁⑹�� 占쎈씨占쎈�� 野����� 占쎌��占쎈�占쎌��嚥∽옙 筌�袁⑼옙
	 */
	private void fillEndTime() {
		int count = body.syncs.size();
		if (count > 0) {
			for (int i = 0; i < count - 1; i++) {
				SyncItem item = body.syncs.get(i);
				if (item.getEndTime() < 0) {
					SyncItem nextItem = body.syncs.get(i + 1);
					item.setEndTime(nextItem.getStartTime() - 1);
				}
			}
			SyncItem lastItem = body.syncs.get(count - 1);
			if (lastItem.getEndTime() < 0)
				lastItem.setEndTime(Long.MAX_VALUE);
		}
	}

	/**
	 * 占쎌��筌�占� 占쎄땀占쎌��占쎈�占쎄� 占쎌��筌�占� 野�占쏙옙源�占쎌�� 占쎈��占쎈��占쎈립占쎈��. 野�占쏙옙源� 占쎌�억옙肉� 獄���諭띰옙�� 占쎌��占쎌�뱄옙留� 占쎄맒占쎄묶占쎈선占쎈� 占쎈립占쎈��.
	 * 
	 * @param time
	 * @return
	 */
	private int binarySearch(long time) { // O(lg(n))
		int first = 0, last = body.syncs.size() - 1;

		while (first <= last) {
			int mid = (first + last) / 2;
			SyncItem item = body.syncs.get(mid);
			if (item.getStartTime() <= time && time < item.getEndTime()) {
				return mid;
			} else if (item.getEndTime() <= time) {
				first = mid + 1;
			} else {
				last = mid - 1;
			}
		}
		return -1;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.getCount(); i++) {
			SyncItem item = this.getItemAtIndex(i);
			sb.append(item.toString()).append("\n");
		}
		return sb.toString();
	}

	@Override
	public boolean hasNext() {
		return body.hasNext();
	}

	@Override
	public SyncItem next() {
		return body.next();
	}

	@Override
	public void remove() {
		body.remove();
	}

	@Override
	public Iterator<SyncItem> iterator() {
		return body;
	}
}
