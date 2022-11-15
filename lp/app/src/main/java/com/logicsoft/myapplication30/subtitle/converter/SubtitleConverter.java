//
// kr.logicsoft.listeningplayer.subtitle.converter;
package com.logicsoft.myapplication30.subtitle.converter;

import android.util.Log;

import com.logicsoft.myapplication30.subtitle.data.IdiomItem;
import com.logicsoft.myapplication30.subtitle.data.LanguageTag;
import com.logicsoft.myapplication30.subtitle.data.Subtitle;
import com.logicsoft.myapplication30.subtitle.data.SubtitleList;
import com.logicsoft.myapplication30.subtitle.data.SyncItem;
import com.logicsoft.myapplication30.subtitle.data.WordItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import kr.logicsoft.listeningplayer.subtitle.data.IdiomItem;
//import kr.logicsoft.listeningplayer.subtitle.data.LanguageTag;
//import kr.logicsoft.listeningplayer.subtitle.data.Subtitle;
//import kr.logicsoft.listeningplayer.subtitle.data.SubtitleList;
//import kr.logicsoft.listeningplayer.subtitle.data.SyncItem;
//import kr.logicsoft.listeningplayer.subtitle.data.WordItem;

public class SubtitleConverter {
	private static DbConnection db = null;

	public static class Sentence {
		private List<WordItem> words = new ArrayList<WordItem>();
		
		public Sentence(List<WordItem> words) {
			this.words.addAll(words);
		}
		
		public List<WordItem> getWords() {
			return this.words;
		}
		
		public WordItem getWord(int index) {
			return words.get(index);
		}
		
		public void addWord(WordItem word) {
			this.words.add(word);
		}
	}
	
	public static class IdiomSearchBean {
		private int idiomId;
		private int idiomOrder;
		private boolean isAttached;
		private int maxOrder;
		private String meaning;
		private int level;
		private WordItem word;
		
		public String getMeaning() {
			return meaning;
		}

		public void setMeaning(String meaning) {
			this.meaning = meaning;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public int getIdiomId() {
			return idiomId;
		}
		
		public void setIdiomId(int idiomId) {
			this.idiomId = idiomId;
		}
		
		public int getIdiomOrder() {
			return idiomOrder;
		}
		
		public void setIdiomOrder(int idiomOrder) {
			this.idiomOrder = idiomOrder;
		}
		
		public boolean isAttached() {
			return isAttached;
		}
		
		public void setAttached(boolean isAttached) {
			this.isAttached = isAttached;
		}
		
		public int getMaxOrder() {
			return maxOrder;
		}
		
		public void setMaxOrder(int maxOrder) {
			this.maxOrder = maxOrder;
		}
		
		public WordItem getWord() {
			return word;
		}
		
		public void setWord(WordItem word) {
			this.word = word;
		}
		
	}
	
	public static void setUp() {
		db = new DbConnection();
	}
	
	public static void teadDown() {
		db.closeConnection();
		db = null;
	}
	
	private static String escape(String value) {
		return value.replaceAll("'", "''");
	}
	
	private static int startIndex = 0;
	
	/**
	 * ������������ ������������������������ ������������ ������������������ ��������� ������ �������������������� ������������������
	 * @param text ������������ ������������������
	 * @return ������������ �����������������
	 */
	private static List<Sentence> splitToSentences(String text) {
		Log.d("subtitle converter", "splittosentences");
		startIndex = 0;
		String escapedText = text.replaceAll("<[^>]+?>", "").replaceAll("&\\w+?;", " ");
        Pattern re = Pattern.compile(
            "# Match a sentence ending in punctuation or EOS.\n" +
            "[^.!?\\s]    # First char is non-punct, non-ws\n" +
            "[^.!?]*      # Greedily consume up to punctuation.\n" +
            "(?:          # Group for unrolling the loop.\n" +
            "  [.!?]      # (special) inner punctuation ok if\n" +
            "  (?!['\"]?\\s|$)  # not followed by ws or EOS.\n" +
            "  [^.!?]*    # Greedily consume up to punctuation.\n" +
            ")*           # Zero or more (special normal*)\n" +
            "[.!?]?       # Optional ending punctuation.\n" +
            "['\"]?       # Optional closing quote.\n" +
            "(?=\\s|$)", //[^.!?\s][^.!?]*(?:[.!?](?!['"]?\s|$)[^.!?]*)*[.!?]?['"]?(?=\s|$)
            Pattern.MULTILINE | Pattern.COMMENTS);
        Matcher reMatcher = re.matcher(escapedText);
        List<Sentence> result = new ArrayList<Sentence>();
        while (reMatcher.find()) {
            result.add(new Sentence(splitToWords(reMatcher.group(), text)));
        }
        return result;
	}
	
	/**
	 * ������������������ ����������������� ������������ ������������������ ��������� ������ �������������������� ������������������
	 * @param sentence ������������������ ������������
	 * @return ������������ �����������������
	 */
	private static List<WordItem> splitToWords(String sentence, String originalText) {

		Pattern re = Pattern.compile("\\b([\\w'-]+)\\b");
		Matcher reMatcher = re.matcher(sentence);
		List<WordItem> result = new ArrayList<WordItem>();
		int order = 0;
		while (reMatcher.find()) {
			WordItem word = new WordItem(reMatcher.group(1));
			if (originalText != null) {
				int actualIndex = originalText.indexOf(reMatcher.group(1), startIndex);
				startIndex += reMatcher.group(1).length();
				word.setStartIndex(actualIndex);
			}
			word.setOrder(order++);
			result.add(word);
		}

		for (WordItem w : result) Log.d("subtitleconverter" , "subtitle-hello-hello" + w.toString());
		return result;
	}
	
	/**
	 * DB������������������ ������������������ ������������������ ������������������
	 * @param variant ������������������ ������������, ������������ ������������������ ������������
	 * @return ������������������ ������������������ ������, ������������
	 */
	private static boolean getOriginalForm(WordItem word) {
		String escapedVariant = escape(word.getVariant());
		String sqlFormat = 
				"SELECT Word, meaningKor, Level " +
				"FROM Words " +
				"WHERE Word = LOWER('%s') OR Word IN(" +
					"SELECT Word " +
					"FROM Variants " +
					"WHERE Variant = LOWER('%s'))";
		String sql = String.format(sqlFormat, escapedVariant, escapedVariant);
		ResultSet rs = null;
		boolean result = false;
		try {
			rs = db.statement.executeQuery(sql);
			while (rs.next()) {
				word.setOriginal(rs.getString("Word"));
				word.setMeaning(rs.getString("meaningKor"));
				word.setLevel(rs.getInt("Level"));
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (rs != null) try { rs.close(); } catch(Exception e) { }
		}
		return result;
	}
	
	/**
	 * �������������������������������� ����������������� ������������ ����������������������� ������������������ (������������ ������������ ������������)
	 * @param sentence ������������������ ������������
	 * @return ������������ �����������������
	 */
	private static List<IdiomItem> getPossibleIdioms(Sentence sentence) {
		String sqlFormat = 
				"SELECT C.IdiomId, C.IdiomOrder, I.IsAttached, I.MaxOrder, I.meaningKor, I.Level " +
				"FROM Consist AS C " +
					"JOIN Idioms AS I " +
						"ON C.IdiomId = I.IdiomId " +
				"WHERE C.Word = LOWER('%s')";
		
		// ������������ ������������ ������������������ ������������, ������ ��������������� ������������������ ������ ������������ ������������ ������������������ ������������������
		List<List<IdiomSearchBean>> allPossibleIdiomsForSentence = new ArrayList<List<IdiomSearchBean>>();
		for (int i = 0; i < sentence.getWords().size(); i++) {
			WordItem word = sentence.getWord(i);
			List<IdiomSearchBean> allPossibleIdiomsForWord = new ArrayList<IdiomSearchBean>();
			
			if (word.getOriginal() != null) {
				String escapedWord = escape(word.getOriginal());
				String sql = String.format(sqlFormat, escapedWord);
				ResultSet rs = null;

				// ������������ ������������������ ������������ ������ ��������������� ������������������ ������ ������������ ������������ ������������ ������������������
				try {
					rs = db.statement.executeQuery(sql);
					
					while (rs.next()) {
						IdiomSearchBean idiomSearch = new IdiomSearchBean();
						idiomSearch.setIdiomId(rs.getInt("IdiomId"));
						idiomSearch.setIdiomOrder(rs.getInt("IdiomOrder"));
						idiomSearch.setAttached(rs.getInt("IsAttached") == 0 ? false : true);
						idiomSearch.setMaxOrder(rs.getInt("MaxOrder"));
						idiomSearch.setMeaning(rs.getString("meaningKor"));
						idiomSearch.setLevel(rs.getInt("Level"));
						idiomSearch.setWord(word);
						allPossibleIdiomsForWord.add(idiomSearch);
					}
				} catch (SQLException se) {
					while (se != null) {
						se = se.getNextException();
					}
				} catch (Exception e) {
					
				} finally {
					if (rs != null) try { rs.close(); } catch (Exception e) { }
				}
			}
			allPossibleIdiomsForSentence.add(allPossibleIdiomsForWord);
		}

		List<IdiomItem> foundIdioms = new ArrayList<IdiomItem>();
		for (int i = 0; i < allPossibleIdiomsForSentence.size(); i++) {
			List<IdiomSearchBean> allPossibleIdiomsForWord = allPossibleIdiomsForSentence.get(i);
			for (int j = 0; j < allPossibleIdiomsForWord.size(); j++) {
				IdiomSearchBean idiomSearch = allPossibleIdiomsForWord.get(j);
				IdiomItem idiom = new IdiomItem(); // ������������������ ������������������ ��������� ������������

				idiom.addWord(idiomSearch.getWord()); // ������������ ������������������ ��������� ������������

				if (idiomSearch.getIdiomOrder() == 0) {
					boolean foundIdiom = false;
					int order = 1;
					boolean searchNext = false;
					int next = 1;
					int attachedWordCount = 0;

					do {
						searchNext = idiomSearch.isAttached();

						if (idiomSearch.getMaxOrder() < order) {
							foundIdiom = true;
							break;
						}
						
						// ������������ ������������������ ������������������ ������������������������ ������������
						if (allPossibleIdiomsForSentence.size() <= i + next) {
							foundIdiom = false;
							break;
						}
						
						List<IdiomSearchBean> nextWordIdioms = allPossibleIdiomsForSentence.get(i + next);

						boolean matchingOrder = false;
						for (int k = 0; k < nextWordIdioms.size(); k++) {
							IdiomSearchBean nextWordIdiom = nextWordIdioms.get(k);
							if ((nextWordIdiom.getIdiomId() == idiomSearch.getIdiomId())
									&& (nextWordIdiom.getIdiomOrder() == order)) {
								idiom.addWord(nextWordIdiom.getWord());
								matchingOrder = true;
								searchNext = true;
								order++;
								break;
							}
						}

						if (searchNext && !matchingOrder) {
							attachedWordCount++;
							if (attachedWordCount > 1) {
								break;
							}
						}
						next++;
					} while (searchNext);

					if (foundIdiom) {
						//idiom.setSyncStart(sentence.getSyncStart());
						//idiom.setSyncEnd(sentence.getSyncEnd());
						idiom.setMeaning(idiomSearch.getMeaning());
						idiom.setLevel(idiomSearch.getLevel());
						//getIdiom(idiom, idiomSearch.getIdiomId());
						foundIdioms.add(idiom);
					}
				}
			}
		}
		
		return foundIdioms;
	}
	
	private static void getIdiom(IdiomItem idiom, int idiomId) {
		String sqlFormat = "SELECT meaningKor, Level FROM Idioms WHERE IdiomId = %d";
		String sql = String.format(sqlFormat, idiomId);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			rs = db.statement.executeQuery(sql);

			if (rs.next()) {
				idiom.setMeaning(rs.getString("meaningKor"));
				idiom.setLevel(rs.getInt("Level"));
			}
		} catch (SQLException se) {
			// Loop through the SQL Exceptions
			while (se != null) {
				se = se.getNextException();
			}
		} catch (Exception e) {

		} finally {
			if (stmt != null) try { stmt.close(); } catch (Exception e) { }
			if (rs != null) try { rs.close(); } catch (Exception e) { }
		}
	}
	
	/**
	 * ������������ ����������������� ������ ������������������ ������������������������ ������������ ����������������� ��������������� ������ ������������������ ��������������� ������������ ������������ ������������������������
	 * ������������ ���������, have to ������ have to do with ������ ������������ ������������, have to ������ ������������������������
	 * @param idioms ������������ �����������������
	 */
	private static void checkDuplicationForIdioms(List<IdiomItem> idioms) {
		for (int i = idioms.size() - 1; i > 0; i--) {
			IdiomItem currentIdiom = idioms.get(i);
			IdiomItem previousIdiom = idioms.get(i - 1);
			if (Math.abs(currentIdiom.getWord(0).getOrder() - previousIdiom.getWord(0).getOrder()) < 2
			/* || curIdiom.lastIndexAtSentence() == preIdiom.lastIndexAtSentence() */) 
			{
				if (currentIdiom.getWords().size() < previousIdiom.getWords().size()) {
					idioms.remove(currentIdiom);
				} else if (currentIdiom.getWords().size() > previousIdiom.getWords().size()) {
					idioms.remove(previousIdiom);
				} else {
					;
				}
			}
		}
	}

	/**
	 * ������������ ����������������������� ����������������� ������������������ ������������ ����������������������������� ������������������������
	 * @param words ������������ �����������������
	 * @param idioms ������������ �����������������
	 */
	private static void checkDuplicationForWords(List<WordItem> words, List<IdiomItem> idioms) {
		for (int i = words.size() - 1; i >= 0; i--) {
			WordItem word = words.get(i);
			for (IdiomItem idiom : idioms) {
				if (idiom.getWords().contains(word)) {
					words.remove(word);
					break;
				}
			}
		}
	}
	
	public static SubtitleList convert(Subtitle sub) {
		Log.d("subtitleconverter", "subtitleconverter convert");
		SubtitleList subs = new SubtitleList();
		Subtitle convSub = new Subtitle();
		convSub.setLanguage(LanguageTag.CONVERTED);
		for (SyncItem sync : sub) {
			List<WordItem> unknownWords = new ArrayList<WordItem>();
			List<Sentence> sentences = splitToSentences(sync.getText());
			
			for (Sentence sentence : sentences) {
				for (WordItem word : sentence.getWords()) {
					if (word.getVariant() != null && word.getVariant().length() > 1) {
						if (getOriginalForm(word) == false) {
							unknownWords.add(word);
						}
					}
				}
				
				List<IdiomItem> idioms = getPossibleIdioms(sentence);
				checkDuplicationForIdioms(idioms);
				//checkDuplicationForWords(sentence.getWords(), idioms);
				
				for (WordItem word : sentence.getWords()) {
					sync.addExtraItem(word);
				}
				
				for (IdiomItem idiom : idioms) {
					sync.addExtraItem(idiom);
				}
			}
			
			convSub.addSyncItem(sync);
		}
		subs.addSubtitle(convSub);

		Log.d("subtitleconverter", "subtitleconverter" + subs.getSubtitle(0));
		StringBuilder sb1 = new StringBuilder();
		Subtitle su = subs.getSubtitle(0);
		Log.d("subtitle", "subtitle size: " + su.body.syncs.size());
		for (SyncItem sy : su.body.syncs) {
			sb1.append("{\"text\":" + "\"" + sy.text + ";");
			sb1.append("\"startTime\":" + sy.startTime + ",\"");
			Log.d("syncitem", "syncitem-hello:" + sy.text);
		}
		return subs;
	}
}
