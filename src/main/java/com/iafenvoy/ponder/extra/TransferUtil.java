package com.iafenvoy.ponder.extra;
//Pick from io.github.fabricators_of_create.porting_lib.transfer

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

@SuppressWarnings({"removal", "unused"})
public class TransferUtil {
	/**
	 * @return Either an outer transaction or a nested one in the current open one
	 */
	public static Transaction getTransaction() {
		if (Transaction.isOpen()) {
			//noinspection deprecation
			TransactionContext open = Transaction.getCurrentUnsafe();
			if (open != null) {
				return open.openNested();
			}
		}
		return Transaction.openOuter();
	}
}

