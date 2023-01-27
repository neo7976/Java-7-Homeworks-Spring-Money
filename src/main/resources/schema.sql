create schema if not exists money_transfer;


update money_transfer.cards c set c.value=:value where c.number=:number

