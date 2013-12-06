/**
 * 
 */
package org.masc.concurrent;

/**
 * Task callback interface
 * 
 * @author masc
 */
public interface TaskCallback<V> {
	void onCompletion(V result, Exception error);
}
