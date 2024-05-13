import { OrderSummaryStatus } from '../../order/shared/order';

export class ChartData {
  values: ChartDataValue[];
  colors: string[];
}

export class ChartDataValue {
  value: number;
  name: string;
}

export class OrderSummaryEntry {
  name: string;
  value?: number;
  children: OrderSummaryEntry[];
}
