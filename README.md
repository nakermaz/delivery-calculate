Если бы мы перенесли тарифы в базу данных,
то смогли бы менять цены через админку без перезапуска приложения.
В этом случае я бы всю логику расчета тоже перенес в базу данных,
чтобы не тащить данные в Java и не считать там.

Примерный вид sql запроса

SELECT
-- Базовая цена
(:distanceKm * :weightTon * br.rateKZT) AS base_price,

    -- Наценка за срочность
    CASE
        WHEN :isUrgent = true
            THEN (:distanceKm * :weightTon  * br.rateKZT) * us.percent / 100
        ELSE 0
        END AS urgent_surcharge,

    -- Наценка за тип груза
    (:distanceKm * :weightTon  * br.rate) * cs.percent / 100 AS cargo_surcharge,

    -- Итоговая цена
    (:distanceKm * :weightTon  * br.rate) * (1 +
                                              CASE WHEN :isUrgent = true THEN us.percent / 100 ELSE 0 END +
                                              cs.percent / 100
        ) AS total_price,

    :currency AS currency

FROM base_rates br
CROSS JOIN cargo_surcharges cs
CROSS JOIN urgent_surcharge us
WHERE br.currency = :currency
AND cs.cargo_type = :cargoType;