import { Container } from "./styles";


export function Status() {

  return (
    <Container>
      <table>
        <thead>
          <tr>
            <th>
              Integrador
            </th>
            <th>
              Última Execução
            </th>
            <th>
              Arquivos Importados
            </th>
            <th>
              Pedidos Gerados
            </th>
            <th>Pré-Vendas Geradas</th>
            <th>
              Retornos Gerados
            </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>EMS</td>
            <td>10/11/2021 08:30</td>
            <td>
              <ul>
                <li>PEDEMS_01260848000112_20200908124543.txt</li>
                <li>PEDEMS_01260848000112_20200908143350.txt</li>
              </ul>
            </td>
            <td>
              <ul>
                <li>19873499</li>
                <li>19873479</li>
              </ul>
            </td>
            <td>
              <ul>
                <li>5000000015</li>
                <li>5000000016</li>
              </ul>
            </td>
            <td>
              <ul>
                <li>RETEMS_01260848000112_20200908124543.txt</li>
                <li>RETEMS_01260848000112_20200908143350.txt</li>
              </ul>
            </td>
          </tr>
          <tr>
            <td>CONSYS</td>
            <td>10/11/2021 10:30</td>
            <td>
              <ul>
                <li>EXPED.PED</li>
              </ul>
            </td>
            <td>
              <ul>
                <li>123456</li>
                <li>654321</li>
              </ul>
            </td>
            <td>
              <ul>
                <li>5000000025</li>
                <li>5000000056</li>
              </ul>
            </td>
            <td>
              <ul>
                <li>EXPED.RET</li>
              </ul>
            </td>
          </tr>
        </tbody>
      </table>
    </Container>
  )
}