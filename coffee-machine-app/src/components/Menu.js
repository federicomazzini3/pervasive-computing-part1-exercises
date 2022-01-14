import React from 'react'
import Drink from './Drink'

const Menu = ({data, onBuy, processingData}) => {
    return (
        <>
            {data.map((drink) => 
                <Drink key={drink.id} drink={drink} onBuy={onBuy} processingData={processingData}/>
            ) }
        </>
    )
}

export default Menu
