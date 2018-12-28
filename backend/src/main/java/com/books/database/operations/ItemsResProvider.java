package com.books.database.operations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.sql.Blob;

import com.books.objects.Story;
import com.books.utils.Constants;

public class ItemsResProvider {

	private static final String UPDATE_ITEM = "UPDATE " +
			Constants.STORY +
			" SET "+
			Constants.LANGUAGE + " =?, " +
			Constants.SYNOPSIS + " =?, " +
			Constants.IMAGE + " =?, " +
			Constants.RATING + " =?, " +
			Constants.STATUS + " =?, " +
			Constants.LIKES +" =? " +
			Constants.AUTHOR + " =?, " +
			Constants.DATE +" =? " +
			" WHERE " + Constants.TITLE + "=?;";

	private static final String GET_ITEMS_BY_USERNAME = "SELECT title FROM  " + Constants.STORY + " WHERE " + Constants.AUTHOR + "=?;";

	private static final String GET_FAVE_ITEMS_BY_USERNAME = "SELECT * FROM  " + Constants.FAVOURITE_STORIES + " WHERE " + Constants.USERNAME + "=?;";

	private static final String GET_ITEM_BY_ID = "SELECT * FROM  " + Constants.STORY + " WHERE " + Constants.TITLE + "=?;";

	private static final String GET_IMAGE =  "SELECT " + Constants.IMAGE + " FROM " + Constants.STORY + " WHERE " + Constants.TITLE + " =?;";

	private static final String INSERT_ITEM = "INSERT INTO " + Constants.STORY + " (" +
			Constants.TITLE + ", " +
			Constants.AUTHOR + ", " +
			Constants.LANGUAGE + ", " +
			Constants.SYNOPSIS + ", " +
			Constants.RATING + " , " +
			Constants.STATUS + ", " +
			Constants.LIKES + ", " +
			Constants.DATE + ", " +
			Constants.IMAGE +
			")" +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

	private static final String DELETE_ITEM_BY_ID = "DELETE FROM " + Constants.STORY + " WHERE " + Constants.TITLE + " =?;";

	private static final String DELETE_ALL_FOR_USER = "DELETE FROM " + Constants.STORY + " WHERE " + Constants.AUTHOR + " =?;";

	private static final String GET_ALL = "SELECT * FROM " + Constants.STORY;

	private static final String GET_NEWEST = "SELECT * FROM " + Constants.STORY + " ORDER BY " + Constants.DATE +" DESC LIMIT 3";

	private static final String REMOVE_FAVE_ITEM = "DELETE FROM " + Constants.FAVOURITE_STORIES + " WHERE " +
				Constants.STORY_TITLE + " =? AND " + Constants.USERNAME + " =?;";

	private static final String REMOVE_FAVE_ITEMS_TITLE = "DELETE FROM " + Constants.FAVOURITE_STORIES + " WHERE " +
			Constants.STORY_TITLE + " =?;";

	private static final String INSERT_FAVE_ITEM = "INSERT INTO " + Constants.FAVOURITE_STORIES + " (" +
			Constants.USERNAME + ", " +
			Constants.STORY_TITLE +
			")" +
			"VALUES (?, ?);";

	private static final String INSERT_CATEGORY = "INSERT INTO " + Constants.CATEGORIES + " (" +
			Constants.STORY_TITLE + ", " +
			Constants.CATEGORY +
			")" +
			"VALUES (?, ?);";

	private static final String DELETE_CATEGORY = "DELETE FROM " + Constants.CATEGORIES + " WHERE " +
			Constants.STORY_TITLE + " =?;";

	private static final String INSERT_TAG = "INSERT INTO " + Constants.TAGS + " (" +
			Constants.STORY_TITLE + ", " +
			Constants.TAG +
			")" +
			"VALUES (?, ?);";

	private static final String DELETE_TAG = "DELETE FROM " + Constants.TAGS + " WHERE " +
			Constants.STORY_TITLE + " =?;";

	private static final String INSERT_GENRE = "INSERT INTO " + Constants.GENRES + " (" +
			Constants.STORY_TITLE + ", " +
			Constants.GENRE +
			")" +
			"VALUES (?, ?);";

	private static final String DELETE_GENRE = "DELETE FROM " + Constants.GENRES + " WHERE " +
			Constants.STORY_TITLE + " =?;";

	private static final String INSERT_CHARACTERS = "INSERT INTO " + Constants.CHARACTERS + " (" +
			Constants.STORY_TITLE + ", " +
			Constants.NAME +
			")" +
			"VALUES (?, ?);";

	private static final String DELETE_CHARACTERS = "DELETE FROM " + Constants.CHARACTERS + " WHERE " +
			Constants.STORY_TITLE + " =?;";

	private static final String INSERT_READING = "INSERT INTO " + Constants.READING_STORIES + " (" +
			Constants.USERNAME + ", " +
			Constants.STORY_TITLE +
			")" +
			"VALUES (?, ?);";

	private static final String DELETE_READING = "DELETE FROM " + Constants.READING_STORIES + " WHERE " +
			Constants.STORY_TITLE + " =?;";

	private static final String DELETE_USER_ALL_READING = "DELETE FROM " + Constants.READING_STORIES + " WHERE " +
			Constants.USERNAME + " =?;";

	private static final String DELETE_READING_ITEM = "DELETE FROM " + Constants.READING_STORIES + " WHERE " +
			Constants.STORY_TITLE + " =? AND " + Constants.USERNAME + "=?; ";

	private static final String GET_LIKES_COUNT = " SELECT " + Constants.LIKES + " FROM " + Constants.STORY
			+ " WHERE " + Constants.TITLE + "=?;";

	private static final String UPDATE_LIKES = " UPDATE " + Constants.STORY + " SET " + Constants.LIKES
			+ " =? WHERE " + Constants.TITLE + "=?;";

	private static final String GET_TAGS = " SELECT * FROM " + Constants.TAGS
			+ " WHERE " + Constants.STORY_TITLE + "=?;";

	private static final String GET_CATES = " SELECT * FROM " + Constants.CATEGORIES
			+ " WHERE " + Constants.STORY_TITLE + "=?;";

	private static final String GET_CHARS = " SELECT * FROM " + Constants.CHARACTERS
			+ " WHERE " + Constants.STORY_TITLE + "=?;";

	private static final String GET_GENRES = " SELECT * FROM " + Constants.GENRES
			+ " WHERE " + Constants.STORY_TITLE + "=?;";

	private static final String GET_USER_ALL_READING = "SELECT * FROM " + Constants.READING_STORIES + " WHERE " +
			Constants.USERNAME + " =?;";

	public boolean insertCategory(String id, String item, Connection conn){
		return insertStuff(id, item, INSERT_CATEGORY, conn);
	}

	public boolean insertGenre(String id, String item, Connection conn){
		return insertStuff(id, item, INSERT_GENRE, conn);
	}

	public boolean insertTagr(String id, String item, Connection conn){
		return insertStuff(id, item, INSERT_TAG, conn);
	}

	public boolean insertReadingr(String username, String sTitle, Connection conn){
		return insertStuff(username, sTitle, INSERT_READING, conn);
	}

	public boolean insertCharacter(String id, String item, Connection conn){
		return insertStuff(id, item, INSERT_CHARACTERS, conn);
	}

	public boolean removeReadingItem(String id, String username, Connection conn){
		boolean result = false;

		PreparedStatement ps = null;

		try {
			// its execute insert
			ps = (PreparedStatement) conn.prepareStatement(DELETE_READING_ITEM);
			ps.setString(1, id);
			ps.setString(2, username);
			ps.execute();

			result = true;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public boolean removeAReadingByStory(String id,  Connection conn){
		return removeStuff(id, DELETE_READING, conn);
	}

	public boolean removeAllCurrentReadings(String username,  Connection conn){
		return removeStuff(username, DELETE_USER_ALL_READING, conn);
	}

	public boolean removeCharacters(String id,  Connection conn){
		return removeStuff(id, DELETE_CHARACTERS, conn);
	}

	public boolean removeGenress(String id,  Connection conn){
		return removeStuff(id, DELETE_GENRE, conn);
	}

	public boolean removeFavess(String id,  Connection conn){
		return removeStuff(id, REMOVE_FAVE_ITEMS_TITLE, conn);
	}

	public boolean removeTags(String id,  Connection conn){
		return removeStuff(id, DELETE_TAG, conn);
	}

	public boolean removeCategory(String id,  Connection conn){
		return removeStuff(id, DELETE_CATEGORY, conn);
	}

	private boolean insertStuff(String id, String item, String op, Connection conn){
		boolean result = false;

		PreparedStatement ps = null;

		try {
			// its execute insert
			ps = (PreparedStatement) conn.prepareStatement(op);
			ps.setString(1, id);
			ps.setString(2, item);
			ps.execute();

			result = true;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	private boolean removeStuff(String id, String op, Connection conn){
		boolean result = false;

		PreparedStatement ps = null;

		try {
			// its execute insert
			ps = (PreparedStatement) conn.prepareStatement(op);
			ps.setString(1, id);
			ps.execute();

			result = true;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public List<Story> getAllItems(Connection conn)
			throws SQLException {
		return prepItems(conn);
	}

	public List<Story> getfAVEItemForUser(String username, Connection conn)
			throws SQLException {
		return getFaveItems(username, conn);
	}

	public List<Story> getItemForUser(String username, Connection conn)
			throws SQLException {
		return getItemsByUser(username, conn);
	}

	private List<Story> getItemsByUser(String username, Connection conn) throws SQLException {

		List<Story> results = new ArrayList<>();
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement ps = null;
		PreparedStatement stt = null;

		String uanme = null;
		byte[] image = null;

		try {

			ps = conn.prepareStatement(GET_ITEMS_BY_USERNAME);
			ps.setString(1, username);

			rs = ps.executeQuery();

			while (rs.next()) {

				String id = rs.getString(Constants.TITLE);
				stt = conn.prepareStatement(GET_ITEM_BY_ID);
                stt.setString(1, id);
				rs1 = stt.executeQuery();

				if(rs1.next()){

					String synopsis = rs1.getString(Constants.SYNOPSIS);
					String rating = rs1.getString(Constants.RATING);
					String language = rs1.getString(Constants.LANGUAGE);
					int likes = rs1.getInt(Constants.LIKES);
					long date = rs1.getLong(Constants.DATE);
					boolean status = rs1.getBoolean(Constants.STATUS);

					Blob imageBlob = rs1.getBlob(Constants.IMAGE);
					image = null;

					if (imageBlob != null) {
						image = imageBlob.getBytes(1, (int) imageBlob.length());
					}

					Story item = new Story(id, username, synopsis, rating, language, likes, status);
					item.setDateFormat(date);
					item.setImage(image);

					item.setTags(getStoryStuff(GET_TAGS, id, Constants.TAG, conn));
					item.setCategories(getStoryStuff(GET_CATES, id, Constants.CATEGORY, conn));
					item.setCharacters(getStoryStuff(GET_CHARS, id, Constants.NAME, conn));
					item.setGenres(getStoryStuff(GET_GENRES, id, Constants.GENRE, conn));

					ChapterResProvidor ch = new ChapterResProvidor();
					item.setChapterCount(ch.getStoryChapters(id, conn).size());
					item.setWordCount(ch.getWordCount(id, conn));

					results.add(item);
				}

			}

		} catch (SQLException e) {
			throw e;

		} catch (Throwable e) {
			e.printStackTrace();

		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return results;

	}

	private List<String> getStoryStuff(String query, String title, String col, Connection conn) throws SQLException {
		List<String> results = new ArrayList<>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		String uanme = null;
		byte[] image = null;

		try {

			ps = conn.prepareStatement(query);
			ps.setString(1, title);
			rs = ps.executeQuery();

			while (rs.next()) {

				String tag = rs.getString(col);

				results.add(tag);

			}

		} catch (SQLException e) {
			throw e;

		} catch (Throwable e) {
			e.printStackTrace();

		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return results;
	}

	private List<Story> prepItems(Connection conn) throws SQLException{
		List<Story> results = new ArrayList<>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		String uanme = null;
		byte[] image = null;

		try {

			ps = conn.prepareStatement(GET_ALL);
			rs = ps.executeQuery();

			while (rs.next()) {

				String title = rs.getString(Constants.TITLE);
				String author = rs.getString(Constants.AUTHOR);
				String rating = rs.getString(Constants.RATING);
				String synopsis = rs.getString(Constants.SYNOPSIS);
				String language = rs.getString(Constants.LANGUAGE);
				int likes = rs.getInt(Constants.LIKES);
				long date = rs.getLong(Constants.DATE);
				boolean status = rs.getBoolean(Constants.STATUS);

				Blob imageBlob = rs.getBlob(Constants.IMAGE);
				image = null;

				if (imageBlob != null) {
					image = imageBlob.getBytes(1, (int) imageBlob.length());
				}

				Story item = new Story(title, author, synopsis, rating, language, likes, status);
				item.setDateFormat(date);
				item.setImage(image);
				item.setTags(getStoryStuff(GET_TAGS, title, Constants.TAG, conn));
				item.setCategories(getStoryStuff(GET_CATES, title, Constants.CATEGORY, conn));
				item.setCharacters(getStoryStuff(GET_CHARS, title, Constants.NAME, conn));
				item.setGenres(getStoryStuff(GET_GENRES, title, Constants.GENRE, conn));

				ChapterResProvidor ch = new ChapterResProvidor();
				item.setChapterCount(ch.getStoryChapters(title, conn).size());
				item.setWordCount(ch.getWordCount(title, conn));
				results.add(item);

			}

		} catch (SQLException e) {
			throw e;

		} catch (Throwable e) {
			e.printStackTrace();

		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return results;
	}

	private List<Story> getFaveItems(String username, Connection conn) throws SQLException{
		List<Story> results = new ArrayList<>();
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement ps = null;
		PreparedStatement stt = null;

		String uanme = null;
		byte[] image = null;

		try {

			ps = conn.prepareStatement(GET_FAVE_ITEMS_BY_USERNAME);
			ps.setString(1, username);

			rs = ps.executeQuery();

			while (rs.next()) {

				String id = rs.getString(Constants.STORY_TITLE);
				stt = conn.prepareStatement(GET_ITEM_BY_ID);
				stt.setString(1, id);
				rs1 = stt.executeQuery();

				if(rs1.next()){

					String synopsis = rs1.getString(Constants.SYNOPSIS);
					String rating = rs1.getString(Constants.RATING);
					String author = rs1.getString(Constants.AUTHOR);
					String language = rs1.getString(Constants.LANGUAGE);
					int likes = rs1.getInt(Constants.LIKES);
					long date = rs1.getLong(Constants.DATE);
					boolean status = rs1.getBoolean(Constants.STATUS);

					Blob imageBlob = rs1.getBlob(Constants.IMAGE);
					image = null;

					if (imageBlob != null) {
						image = imageBlob.getBytes(1, (int) imageBlob.length());
					}

					Story item = new Story(id, author, synopsis, rating, language, likes, status);
					item.setDateFormat(date);
					item.setImage(image);
					item.setTags(getStoryStuff(GET_TAGS, id, Constants.TAG, conn));
					item.setCategories(getStoryStuff(GET_CATES, id, Constants.CATEGORY, conn));
					item.setCharacters(getStoryStuff(GET_CHARS, id, Constants.NAME, conn));
					item.setGenres(getStoryStuff(GET_GENRES, id, Constants.GENRE, conn));

					ChapterResProvidor ch = new ChapterResProvidor();
					item.setChapterCount(ch.getStoryChapters(id, conn).size());
					item.setWordCount(ch.getWordCount(id, conn));

					results.add(item);
				}

			}

		} catch (SQLException e) {
			throw e;

		} catch (Throwable e) {
			e.printStackTrace();

		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return results;
	}

	public byte[] getImage(String itemId, Connection conn)
			throws SQLException {

		byte[] result = null;

		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
		
			ps = conn.prepareStatement(GET_IMAGE);
			ps.setString(1, itemId);
			rs = ps.executeQuery();

			while (rs.next()) {
				Blob imageBlob = rs.getBlob(1);
				if (imageBlob != null) {
					result = imageBlob.getBytes(1, (int) imageBlob.length());
				}
			}

		} catch (SQLException e) {
			throw e;

		} catch (Throwable e) {
			e.printStackTrace();

		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	public boolean removeFaveItem(String id, String username, Connection conn){
		boolean result = false;

		PreparedStatement ps = null, ps2 = null;
		ResultSet rs = null;
		try {
			// its execute insert
			ps = (PreparedStatement) conn.prepareStatement(REMOVE_FAVE_ITEM);
			ps.setString(1, id);
			ps.setString(2, username);
			ps.execute();

			ps2 = (PreparedStatement) conn.prepareStatement(GET_LIKES_COUNT);
			ps2.setString(1, id);
			rs = ps2.executeQuery();
			if (rs.next()){
				int likes = rs.getInt(Constants.LIKES);
				ps = conn.prepareStatement(UPDATE_LIKES);
				ps.setInt(1,--likes);
				ps.setString(2, id);
				ps.execute();
			}
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps2 != null) {
				try {
					ps2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public boolean insertFaveItem(String id, String username, Connection conn){
		boolean result = false;
		PreparedStatement ps = null, ps2 = null;
		ResultSet rs = null;

		try {
			// its execute insert
			ps = (PreparedStatement) conn.prepareStatement(INSERT_FAVE_ITEM);
			ps.setString(1, username);
			ps.setString(2, id);
			ps.execute();
			ps2 = (PreparedStatement) conn.prepareStatement(GET_LIKES_COUNT);
			ps2.setString(1, id);
			rs = ps2.executeQuery();
			if (rs.next()){
				int likes = rs.getInt(Constants.LIKES);
				ps = conn.prepareStatement(UPDATE_LIKES);
				ps.setInt(1,++likes);
				ps.setString(2, id);
				ps.execute();

			}
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps2 != null) {
				try {
					ps2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	public boolean insertItem(Story obj, String cates, String tags, String chars, String genres, Connection conn) {

		boolean result = false;
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement ps = null;
		PreparedStatement stt = null;

		try {

			String title = obj.getTitle();
			String author = obj.getAuthor();
			String rating = obj.getRating();
			String synopsis = obj.getSynopsis();
			int likes = obj.getLikes();
			String language = obj.getLanguage();
			boolean status = obj.getStatusBool();
			long date = obj.getDateFormat();

			byte[] imageBytes = obj.getImage();

			if (imageBytes == null) {
				imageBytes = getImage(title, conn);
			}

			stt = (PreparedStatement) conn.prepareStatement(GET_ITEM_BY_ID);
			stt.setString(1, title);

			if (stt.execute()) {
				rs1 = stt.getResultSet();
				if (rs1.next()) {
					// its execute update
					ps = (PreparedStatement) conn.prepareStatement(UPDATE_ITEM);

					ps.setString(1, language);
					ps.setString(2, synopsis);
					ps.setString(4, rating);
					ps.setInt(6, likes);
					ps.setString(7, author);
					ps.setBoolean(5, status);
					ps.setLong(8, date);

					if (imageBytes != null) {
						InputStream is = new ByteArrayInputStream(imageBytes);
						ps.setBlob(3, is);

					} else {

						ps.setNull(3, Types.BLOB);
					}
					// where
					ps.setString(8, title);
					ps.execute();
					removeCharacters(title,conn);
					removeGenress(title, conn);
					removeCategory(title, conn);
					removeTags(title, conn);
					for (String s : chars.split(", "))
						insertCharacter(title, s, conn);
					for (String s : cates.split(", "))
						insertCategory(title, s, conn);
					for (String s : tags.split(", "))
						insertTagr(title, s, conn);
					for (String s : genres.split(", "))
						insertGenre(title, s, conn);


					result = true;

				} else {

					// its execute insert
					ps = (PreparedStatement) conn.prepareStatement(INSERT_ITEM);

					ps.setString(1, title);
					ps.setString(2, author);
					ps.setString(3, language);
					ps.setString(4, synopsis);
					ps.setString(5, rating);
					ps.setBoolean(6, status);
					ps.setInt(7, likes);
					ps.setLong(8, date);

					if (imageBytes != null) {
						InputStream is = new ByteArrayInputStream(imageBytes);
						ps.setBlob(9, is);

					} else {
						ps.setNull(9, Types.BLOB);
					}
					ps.execute();

						for (String s : chars.split(", "))
							insertCharacter(title, s, conn);
						for (String s : cates.split(", "))
							insertCategory(title, s, conn);
						for (String s : tags.split(", "))
							insertTagr(title, s, conn);
						for (String s : genres.split(", "))
							insertGenre(title, s, conn);

					result = true;

				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stt != null) {
				try {
					stt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public boolean deleteAllItemsByUserId(String username, Connection conn)
			throws SQLException {

		boolean result = false;

		PreparedStatement ps = null;

		try {
			ps = (PreparedStatement) conn.prepareStatement(DELETE_ALL_FOR_USER);
			ps.setString(1, username);
			ps.execute();
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public boolean deleteItem(Story obj, Connection conn) throws SQLException {

		boolean result = false;
		PreparedStatement ps = null;

		try {
			if (obj != null) {

				String id = obj.getTitle();
				ps = (PreparedStatement) conn.prepareStatement(DELETE_ITEM_BY_ID);
				ps.setString(1, id);
				ps.execute();

				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {

					e.printStackTrace();
				}
			}
		}
		return result;
	}

    public Story getStoryByTitle(String sTitle, Connection conn) throws SQLException {
		Story story = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String uanme = null;
		byte[] image = null;

		try {

			ps = conn.prepareStatement(GET_ITEM_BY_ID);
			ps.setString(1,  sTitle);
			rs = ps.executeQuery();

			while (rs.next()) {

				String title = rs.getString(Constants.TITLE);
				String author = rs.getString(Constants.AUTHOR);
				String rating = rs.getString(Constants.RATING);
				String synopsis = rs.getString(Constants.SYNOPSIS);
				String language = rs.getString(Constants.LANGUAGE);
				int likes = rs.getInt(Constants.LIKES);
				long date = rs.getLong(Constants.DATE);
				boolean status = rs.getBoolean(Constants.STATUS);

				Blob imageBlob = rs.getBlob(Constants.IMAGE);
				image = null;

				if (imageBlob != null) {
					image = imageBlob.getBytes(1, (int) imageBlob.length());
				}

				Story item = new Story(title, author, synopsis, rating, language, likes, status);
				item.setDateFormat(date);
				item.setImage(image);
				item.setTags(getStoryStuff(GET_TAGS, title, Constants.TAG, conn));
				item.setCategories(getStoryStuff(GET_CATES, title, Constants.CATEGORY, conn));
				item.setCharacters(getStoryStuff(GET_CHARS, title, Constants.NAME, conn));
				item.setGenres(getStoryStuff(GET_GENRES, title, Constants.GENRE, conn));

				ChapterResProvidor ch = new ChapterResProvidor();
				item.setChapterCount(ch.getStoryChapters(title, conn).size());
				item.setWordCount(ch.getWordCount(title, conn));

				story = item;

			}

		} catch (SQLException e) {
			throw e;

		} catch (Throwable e) {
			e.printStackTrace();

		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return story;
    }

	public List<Story> getNewestItems(Connection conn) throws SQLException {
		List<Story> results = new ArrayList<>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		String uanme = null;
		byte[] image = null;

		try {

			ps = conn.prepareStatement(GET_NEWEST);
			rs = ps.executeQuery();

			while (rs.next()) {

				String title = rs.getString(Constants.TITLE);
				String author = rs.getString(Constants.AUTHOR);
				String rating = rs.getString(Constants.RATING);
				String synopsis = rs.getString(Constants.SYNOPSIS);
				String language = rs.getString(Constants.LANGUAGE);
				int likes = rs.getInt(Constants.LIKES);
				long date = rs.getLong(Constants.DATE);
				boolean status = rs.getBoolean(Constants.STATUS);

				Blob imageBlob = rs.getBlob(Constants.IMAGE);
				image = null;

				if (imageBlob != null) {
					image = imageBlob.getBytes(1, (int) imageBlob.length());
				}

				Story item = new Story(title, author, synopsis, rating, language, likes, status);
				item.setDateFormat(date);
				item.setImage(image);
				item.setTags(getStoryStuff(GET_TAGS, title, Constants.TAG, conn));
				item.setCategories(getStoryStuff(GET_CATES, title, Constants.CATEGORY, conn));
				item.setCharacters(getStoryStuff(GET_CHARS, title, Constants.NAME, conn));
				item.setGenres(getStoryStuff(GET_GENRES, title, Constants.GENRE, conn));

				ChapterResProvidor ch = new ChapterResProvidor();
				item.setChapterCount(ch.getStoryChapters(title, conn).size());
				item.setWordCount(ch.getWordCount(title, conn));
				results.add(item);

			}

		} catch (SQLException e) {
			throw e;

		} catch (Throwable e) {
			e.printStackTrace();

		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return results;
	}

    public List<Story> getCurrentReadingStories(String username, Connection conn) throws SQLException {
		List<Story> results = new ArrayList<>();
		ResultSet rs = null, rs1 = null;
		PreparedStatement ps = null, stt = null;
		String uanme = null;
		byte[] image = null;

		try {

			ps = conn.prepareStatement(GET_USER_ALL_READING);
			ps.setString(1, username);
			rs = ps.executeQuery();

			while (rs.next()) {

				String id = rs.getString(Constants.STORY_TITLE);
				stt = conn.prepareStatement(GET_ITEM_BY_ID);
				stt.setString(1, id);
				rs1 = stt.executeQuery();

				if(rs1.next()){

					String synopsis = rs1.getString(Constants.SYNOPSIS);
					String rating = rs1.getString(Constants.RATING);
					String author = rs1.getString(Constants.AUTHOR);
					String language = rs1.getString(Constants.LANGUAGE);
					int likes = rs1.getInt(Constants.LIKES);
					long date = rs1.getLong(Constants.DATE);
					boolean status = rs1.getBoolean(Constants.STATUS);

					Blob imageBlob = rs1.getBlob(Constants.IMAGE);
					image = null;

					if (imageBlob != null) {
						image = imageBlob.getBytes(1, (int) imageBlob.length());
					}

					Story item = new Story(id, author, synopsis, rating, language, likes, status);
					item.setDateFormat(date);
					item.setImage(image);
					item.setTags(getStoryStuff(GET_TAGS, id, Constants.TAG, conn));
					item.setCategories(getStoryStuff(GET_CATES, id, Constants.CATEGORY, conn));
					item.setCharacters(getStoryStuff(GET_CHARS, id, Constants.NAME, conn));
					item.setGenres(getStoryStuff(GET_GENRES, id, Constants.GENRE, conn));

					ChapterResProvidor ch = new ChapterResProvidor();
					item.setChapterCount(ch.getStoryChapters(id, conn).size());
					item.setWordCount(ch.getWordCount(id, conn));

					results.add(item);
				}

			}

		} catch (SQLException e) {
			throw e;

		} catch (Throwable e) {
			e.printStackTrace();

		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return results;
    }
}
