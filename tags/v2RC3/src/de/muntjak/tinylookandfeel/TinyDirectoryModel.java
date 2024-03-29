/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.plaf.basic.BasicFileChooserUI;

/**
 * A copy of BasicDirectoryModel with access to the fileCache.
 * 
 * @author Hans Bickel
 * 
 */
public class TinyDirectoryModel extends BasicDirectoryModel {

	class DoChangeContents implements Runnable {

		private final List addFiles;

		private final List remFiles;

		private boolean doFire = true;

		private final int fid;

		private int addStart = 0;

		private int remStart = 0;

		public DoChangeContents(final List addFiles, final int addStart, final List remFiles,
				final int remStart, final int fid) {
			this.addFiles = addFiles;
			this.addStart = addStart;
			this.remFiles = remFiles;
			this.remStart = remStart;
			this.fid = fid;
		}

		synchronized void cancel() {
			doFire = false;
		}

		@Override
		public synchronized void run() {
			if (fetchID == fid && doFire) {
				final int remSize = (remFiles == null) ? 0 : remFiles.size();
				final int addSize = (addFiles == null) ? 0 : addFiles.size();
				synchronized (fileCache) {
					if (remSize > 0) {
						fileCache.removeAll(remFiles);
					}
					if (addSize > 0) {
						fileCache.addAll(addStart, addFiles);
					}
					files = null;
					directories = null;
				}
				if (remSize > 0 && addSize == 0) {
					fireIntervalRemoved(TinyDirectoryModel.this, remStart,
							remStart + remSize - 1);
				} else if (addSize > 0 && remSize == 0
						&& fileCache.size() > addSize) {
					fireIntervalAdded(TinyDirectoryModel.this, addStart,
							addStart + addSize - 1);
				} else {
					fireContentsChanged();
				}
			}
		}
	}

	class LoadFilesThread extends Thread {

		File currentDirectory = null;

		int fid;

		Vector runnables = new Vector(10);

		public LoadFilesThread(final File currentDirectory, final int fid) {
			super("Basic L&F File Loading Thread");
			this.currentDirectory = currentDirectory;
			this.fid = fid;
		}

		public void cancelRunnables() {
			cancelRunnables(runnables);
		}

		public void cancelRunnables(final Vector runnables) {
			for (int i = 0; i < runnables.size(); i++) {
				((DoChangeContents) runnables.elementAt(i)).cancel();
			}
		}

		private void invokeLater(final Runnable runnable) {
			runnables.addElement(runnable);
			SwingUtilities.invokeLater(runnable);
		}

		@Override
		public void run() {
			run0();
			setBusy(false, fid);
		}

		public void run0() {
			final FileSystemView fileSystem = filechooser.getFileSystemView();

			final File[] list = fileSystem.getFiles(currentDirectory,
					filechooser.isFileHidingEnabled());

			final Vector acceptsList = new Vector();

			if (isInterrupted()) {
				return;
			}

			// run through the file list, add directories and selectable files
			// to fileCache
			for (int i = 0; i < list.length; i++) {
				if (filechooser.accept(list[i])) {
					acceptsList.addElement(list[i]);
				}
			}

			if (isInterrupted()) {
				return;
			}

			// First sort alphabetically by filename
			sort(acceptsList);

			final Vector newDirectories = new Vector(50);
			final Vector newFiles = new Vector();
			// run through list grabbing directories in chunks of ten
			for (int i = 0; i < acceptsList.size(); i++) {
				final File f = (File) acceptsList.elementAt(i);
				final boolean isTraversable = filechooser.isTraversable(f);
				if (isTraversable) {
					newDirectories.addElement(f);
				} else if (!isTraversable
						&& filechooser.isFileSelectionEnabled()) {
					newFiles.addElement(f);
				}
				if (isInterrupted()) {
					return;
				}
			}

			Vector newFileCache = new Vector(newDirectories);
			newFileCache.addAll(newFiles);

			final int newSize = newFileCache.size();
			final int oldSize = fileCache.size();

			if (newSize > oldSize) {
				// see if interval is added
				int start = oldSize;
				int end = newSize;
				for (int i = 0; i < oldSize; i++) {
					if (!newFileCache.get(i).equals(fileCache.get(i))) {
						start = i;
						for (int j = i; j < newSize; j++) {
							if (newFileCache.get(j).equals(fileCache.get(i))) {
								end = j;
								break;
							}
						}
						break;
					}
				}
				if (start >= 0
						&& end > start
						&& newFileCache.subList(end, newSize).equals(
								fileCache.subList(start, oldSize))) {
					if (isInterrupted()) {
						return;
					}
					invokeLater(new DoChangeContents(newFileCache.subList(
							start, end), start, null, 0, fid));
					newFileCache = null;
				}
			} else if (newSize < oldSize) {
				// see if interval is removed
				int start = -1;
				int end = -1;
				for (int i = 0; i < newSize; i++) {
					if (!newFileCache.get(i).equals(fileCache.get(i))) {
						start = i;
						end = i + oldSize - newSize;
						break;
					}
				}
				if (start >= 0
						&& end > start
						&& fileCache.subList(end, oldSize).equals(
								newFileCache.subList(start, newSize))) {
					if (isInterrupted()) {
						return;
					}
					invokeLater(new DoChangeContents(null, 0, new Vector(
							fileCache.subList(start, end)), start, fid));
					newFileCache = null;
				}
			}
			if (newFileCache != null && !fileCache.equals(newFileCache)) {
				if (isInterrupted()) {
					cancelRunnables(runnables);
				}
				invokeLater(new DoChangeContents(newFileCache, 0, fileCache, 0,
						fid));
			}
		}
	}

	private JFileChooser filechooser = null;

	// PENDING(jeff) pick the size more sensibly
	private final Vector fileCache = new Vector(50);

	private LoadFilesThread loadThread = null;

	private Vector files = null;

	private Vector directories = null;

	private int fetchID = 0;

	private PropertyChangeSupport changeSupport;

	private boolean busy = false;

	public TinyDirectoryModel(final JFileChooser fc) {
		super(fc);

		this.filechooser = fc;
		validateFileCache();
	}

	/**
	 * Adds a PropertyChangeListener to the listener list. The listener is
	 * registered for all bound properties of this class.
	 * <p>
	 * If <code>listener</code> is <code>null</code>, no exception is thrown and
	 * no action is performed.
	 * 
	 * @param listener
	 *            the property change listener to be added
	 * 
	 * @see #removePropertyChangeListener
	 * @see #getPropertyChangeListeners
	 * 
	 * @since 1.6
	 */
	@Override
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		if (changeSupport == null) {
			changeSupport = new PropertyChangeSupport(this);
		}
		changeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public boolean contains(final Object o) {
		return fileCache.contains(o);
	}

	@Override
	public void fireContentsChanged() {
		// System.out.println("TinyDirectoryModel: firecontentschanged");
		fireContentsChanged(this, 0, getSize() - 1);
	}

	/**
	 * Support for reporting bound property changes for boolean properties. This
	 * method can be called when a bound property has changed and it will send
	 * the appropriate PropertyChangeEvent to any registered
	 * PropertyChangeListeners.
	 * 
	 * @param propertyName
	 *            the property whose value has changed
	 * @param oldValue
	 *            the property's previous value
	 * @param newValue
	 *            the property's new value
	 * 
	 * @since 1.6
	 */
	protected void firePropertyChange(final String propertyName, final boolean oldValue,
			final boolean newValue) {
		if (changeSupport != null) {
			changeSupport.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	@Override
	public Vector getDirectories() {
		synchronized (fileCache) {
			if (directories != null) {
				return directories;
			}
			getFiles();
			return directories;
		}
	}

	@Override
	public Object getElementAt(final int index) {
		return fileCache.get(index);
	}

	public Vector getFileCache() {
		return fileCache;
	}

	@Override
	public Vector getFiles() {
		synchronized (fileCache) {
			if (files != null) {
				return files;
			}
			files = new Vector();
			directories = new Vector();
			directories.addElement(filechooser.getFileSystemView()
					.createFileObject(filechooser.getCurrentDirectory(), ".."));

			for (int i = 0; i < getSize(); i++) {
				final File f = (File) fileCache.get(i);
				if (filechooser.isTraversable(f)) {
					directories.add(f);
				} else {
					files.add(f);
				}
			}
			return files;
		}
	}

	/**
	 * Returns an array of all the property change listeners registered on this
	 * component.
	 * 
	 * @return all of this component's <code>PropertyChangeListener</code>s or
	 *         an empty array if no property change listeners are currently
	 *         registered
	 * 
	 * @see #addPropertyChangeListener
	 * @see #removePropertyChangeListener
	 * @see java.beans.PropertyChangeSupport#getPropertyChangeListeners
	 * 
	 * @since 1.6
	 */
	@Override
	public PropertyChangeListener[] getPropertyChangeListeners() {
		if (changeSupport == null) {
			return new PropertyChangeListener[0];
		}
		return changeSupport.getPropertyChangeListeners();
	}

	@Override
	public int getSize() {
		return fileCache.size();
	}

	@Override
	public int indexOf(final Object o) {
		return fileCache.indexOf(o);
	}

	/**
	 * Obsolete - not used.
	 */
	@Override
	public void intervalAdded(final ListDataEvent e) {
	}

	/**
	 * Obsolete - not used.
	 */
	@Override
	public void intervalRemoved(final ListDataEvent e) {
	}

	/**
	 * This method is used to interrupt file loading thread.
	 */
	@Override
	public void invalidateFileCache() {
		if (loadThread != null) {
			loadThread.interrupt();
			loadThread.cancelRunnables();
			loadThread = null;
		}
	}

	// Obsolete - not used
	@Override
	protected boolean lt(final File a, final File b) {
		// First ignore case when comparing
		final int diff = a.getName().toLowerCase()
		.compareTo(b.getName().toLowerCase());
		if (diff != 0) {
			return diff < 0;
		} else {
			// May differ in case (e.g. "mail" vs. "Mail")
			return a.getName().compareTo(b.getName()) < 0;
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent e) {
		final String prop = e.getPropertyName();
		if (prop == JFileChooser.DIRECTORY_CHANGED_PROPERTY
				|| prop == JFileChooser.FILE_VIEW_CHANGED_PROPERTY
				|| prop == JFileChooser.FILE_FILTER_CHANGED_PROPERTY
				|| prop == JFileChooser.FILE_HIDING_CHANGED_PROPERTY
				|| prop == JFileChooser.FILE_SELECTION_MODE_CHANGED_PROPERTY) {
			validateFileCache();
		} else if ("UI".equals(prop)) {
			final Object old = e.getOldValue();
			if (old instanceof BasicFileChooserUI) {
				final BasicFileChooserUI ui = (BasicFileChooserUI) old;
				final BasicDirectoryModel model = ui.getModel();
				if (model != null) {
					model.invalidateFileCache();
				}
			}
		} else if ("JFileChooserDialogIsClosingProperty".equals(prop)) {
			invalidateFileCache();
		}
	}

	/**
	 * Removes a PropertyChangeListener from the listener list.
	 * <p>
	 * If listener is null, no exception is thrown and no action is performed.
	 * 
	 * @param listener
	 *            the PropertyChangeListener to be removed
	 * 
	 * @see #addPropertyChangeListener
	 * @see #getPropertyChangeListeners
	 * 
	 * @since 1.6
	 */
	@Override
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		if (changeSupport != null) {
			changeSupport.removePropertyChangeListener(listener);
		}
	}

	/**
	 * Renames a file in the underlying file system.
	 * 
	 * @param oldFile
	 *            a <code>File</code> object representing the existing file
	 * @param newFile
	 *            a <code>File</code> object representing the desired new file
	 *            name
	 * @return <code>true</code> if rename succeeded, otherwise
	 *         <code>false</code>
	 * @since 1.4
	 */
	@Override
	public boolean renameFile(final File oldFile, final File newFile) {
		synchronized (fileCache) {
			if (oldFile.renameTo(newFile)) {
				validateFileCache();
				return true;
			}
			return false;
		}
	}

	/**
	 * Set the busy state for the model. The model is considered busy when it is
	 * running a separate (interruptable) thread in order to load the contents
	 * of a directory.
	 */
	private synchronized void setBusy(final boolean busy, final int fid) {
		if (fid == fetchID) {
			final boolean oldValue = this.busy;
			this.busy = busy;

			if (changeSupport != null && busy != oldValue) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						firePropertyChange("busy", !busy, busy);
					}
				});
			}
		}
	}


	@Override
	public void validateFileCache() {
		// Note: The super constructor will call this method
		// but at this time filechooser is null
		if (filechooser == null)
			return;

		final File currentDirectory = filechooser.getCurrentDirectory();
		if (currentDirectory == null) {
			return;
		}
		if (loadThread != null) {
			loadThread.interrupt();
			loadThread.cancelRunnables();
		}

		setBusy(true, ++fetchID);

		loadThread = new LoadFilesThread(currentDirectory, fetchID);
		loadThread.start();
	}
}
