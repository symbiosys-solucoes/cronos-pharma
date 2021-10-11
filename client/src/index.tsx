import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import { createServer } from 'miragejs'


createServer(
  {
    routes(){
      this.namespace = 'api/v1'

      this.get('status', () => {
        return [
          {
            data_ultima_execucao: '10/11/2021 08:31',
            arquivos: [
              'PEDEMS_01260848000112_20200908124543.txt',
              'PEDEMS_01260848000112_20200908143350.txt'
            ],
            pedidos_gerados: [
              '19873499',
              '19873479'
            ],
            prevendas_geradas: [
              '5000000015',
              '5000000016'              
            ],
            integrador: 'EMS'
          },
          {
            data_ultima_execucao: '10/11/2021 10:31',
            arquivos: [
              'EXPPED.PED'
            ],
            pedidos_gerados: [
              '123',
              '456'
            ],
            prevendas_geradas: [
              '5000000025',
              '5000000055'              
            ],
            integrador: 'CONSYS'
          }
        ]
      })
    }
  }
)

ReactDOM.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
  document.getElementById('root')
);

