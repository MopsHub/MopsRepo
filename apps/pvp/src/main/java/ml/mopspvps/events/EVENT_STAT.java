package ml.mopspvps.events;

/**
 * Состояние ивентов
 */
public enum EVENT_STAT {
	/**
	 * Ивент был отменен
	 */
	CANCELED,
	/**
	 * Ивент был успешно обработан
	 */
	HANDLED,
	/**
	 * Ивент не обрабатывается в коде, или в коде произошла ошибка
	 */
	NOT_HANDLED,
	/**
	 * Произошла ошибка
	 */
	THROWABLE
}
